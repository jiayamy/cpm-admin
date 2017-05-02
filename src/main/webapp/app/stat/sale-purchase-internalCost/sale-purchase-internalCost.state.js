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
            url: '/sale-purchase-internalCost?contractId&userId&statWeek&deptType&userName',
            data: {
                authorities: ['ROLE_STAT_INTERNAL_COST'],
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
                contractId : null,
                userId : null,
                userName : null,
                statWeek : null,
                deptType : null
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    return $ocLazyLoad.load([
                                             'app/stat/sale-purchase-internalCost/sale-purchase-internalCost.service.js',
                                             'app/stat/sale-purchase-internalCost/sale-purchase-internalCost.controller.js',
                                             'app/project/project-info/project-info.service.js',
                                             'app/info/dept-type/dept-type.service.js']);
                }],
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        contractId: $stateParams.contractId,
                        userId: $stateParams.userId,
                        userName: $stateParams.userName,
                        statWeek : $stateParams.statWeek,
                        deptType : $stateParams.deptType
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('salePurchaseInternalCost');
                    $translatePartialLoader.addPart('deptInfo');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('sale-purchase-internalCost.queryDept', {
            parent: 'sale-purchase-internalCost',
            url: '/queryDept?selectType&showChild',
            data: {
                authorities: ['ROLE_STAT_INTERNAL_COST']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/info/dept-info/dept-info-query.html',
                    controller: 'DeptInfoQueryController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                    	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                            return $ocLazyLoad.load([
                                                     'app/info/dept-info/dept-info-query.controller.js',
                                                     'app/info/dept-info/dept-info.service.js'
                                                     ]);
                        }],
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
        	url: '/detail?page&id',
        	data:{
        		authorities: ['ROLE_STAT_INTERNAL_COST'],
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
                id: null,
                contractId : null,
                userId : null,
                userName : null,
                statWeek : null,
                deptType : null
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    return $ocLazyLoad.load('app/stat/sale-purchase-internalCost/sale-purchase-internalCost-detail.controller.js');
                }],
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        id: $stateParams.id,
                        contractId: $stateParams.contractId,
                        userId: $stateParams.userId,
                        userName: $stateParams.userName,
                        statWeek : $stateParams.statWeek,
                        deptType : $stateParams.deptType
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
