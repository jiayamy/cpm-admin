(function() {
    'use strict';

    angular
        .module('cpmApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('sale-purchase-internalCost', {
            parent: 'stat',
            url: '/sale-purchase-internalCost?page&contractId&userNameId&statWeek&deptType&userName',
            data: {
                authorities: ['ROLE_STAT_PROJECT'],
                pageTitle: 'cpmApp.salePurchaseInternalCost.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/stat/sale-purchase-internalCost/sale-purchase-internalCost.html',
                    controller: 'SalePurchaseInternalCostController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'p.id,desc',
                    squash: true
                },
                contractId : null,
                userNameId : null,
                userName : null,
                statWeek : null,
                deptType : null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        contractId: $stateParams.contractId,
                        userNameId: $stateParams.userNameId,
                        userName: $stateParams.userName,
                        statWeek : $stateParams.statWeek,
                        deptType : $stateParams.deptType
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('salePurchaseInternalCost');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('sale-purchase-internalCost.queryDept', {
            parent: 'sale-purchase-internalCost',
            url: '/queryDept?selectType&showChild',
            data: {
                authorities: ['ROLE_STAT_PROJECT']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/info/dept-info/dept-info-query.html',
                    controller: 'DeptInfoQueryController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function() {
                            return {
                            	selectType : $stateParams.selectType,
                            	showChild : $stateParams.showChild
                            }
                        }
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('sale-purchase-internalCost.Detail',{
        	parent: 'sale-purchase-internalCost',
        	url: '/detail?page&contId',
        	data:{
        		authorities: ['ROLE_STAT_PROJECT'],
        		pageTitle: 'cpmApp.consultantBonus.contractRecord.title'
        	},
        	views:{
        		'content@':{
        			templateUrl: 'app/stat/sale-purchase-internalCost/sale-purchase-internalCost-detail.html',
                    controller: 'SalePurchaseInternalCostDetailController',
                    controllerAs: 'vm'
        		}
        	},
        	params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'p.id,desc',
                    squash: true
                },
                contId: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        contId: $stateParams.contId
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('salePurchaseInternalCost');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'sale-purchase-internalCost',
                        params: $state.params,
                        url: $state.href($state.current.name , $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
    }
})();
