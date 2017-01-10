(function() {
    'use strict';

    angular
        .module('cpmApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('contract-info', {
            parent: 'contract',
            url: '/contract-info?page&sort&search&name&type&isPrepared&isEpibolic&salesman',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'cpmApp.contractInfo.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/contract/contract-info/contract-infos.html',
                    controller: 'ContractInfoController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                },
                name:null,
                type:null,
                isPrepared:null,
                isEpibolic:null,
                salesman:null
            },
            resolve: {
                pagingParams: ['$state','$stateParams', 'PaginationUtil', function ($state,$stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        name: $stateParams.name,
                        type: $stateParams.type,
                        isPrepared: $stateParams.isPrepared,
                        isEpibolic: $stateParams.isEpibolic,
                        salesman: $stateParams.salesman
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('contractInfo');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('contract-info-detail', {
            parent: 'contract',
            url: '/contract-info/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'cpmApp.contractInfo.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/contract/contract-info/contract-info-detail.html',
                    controller: 'ContractInfoDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('contractInfo');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'ContractInfo', function($stateParams, ContractInfo) {
                    return ContractInfo.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'contract-info',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
       .state('contract-info-detail.edit',{
        	parent:'contract-info',
        	url:'/{id}/edit',
        	data:{
        		authorities: ['ROLE_USER']
        	},
        	views:{
        		'content@':{
        			templateUrl: 'app/contract/contract-info/contract-info-dialog.html',
        			controller: 'ContractInfoDialogController',
        			controllerAs: 'vm'
        		}
        	},
        	resolve:{
        		 translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                     $translatePartialLoader.addPart('contractInfo');
                     return $translate.refresh();
                 }],
                 entity: ['$stateParams', 'ContractInfo', function($stateParams, ContractInfo) {
                     return ContractInfo.get({id : $stateParams.id}).$promise;
                 }]
        	}
        })
        
        .state('contract-info.new',{
        	parent: 'contract-info',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            views:{
            	'content@':{
            		templateUrl: 'app/contract/contract-info/contract-info-dialog.html',
                    controller: 'ContractInfoDialogController',
                    controllerAs: 'vm'
            	}
            },
            resolve: {
            	 translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                     $translatePartialLoader.addPart('contractInfo');
                     return $translate.refresh();
                 }],
                entity: function () {
                    return {
                        serialNum: null,
                        name: null,
                        amount: null,
                        type: null,
                        isPrepared: null,
                        isEpibolic: null,
                        startDay: null,
                        endDay: null,
                        taxRate: null,
                        taxes: null,
                        shareRate: null,
                        shareCost: null,
                        paymentWay: null,
                        contractor: null,
                        address: null,
                        postcode: null,
                        linkman: null,
                        contactDept: null,
                        telephone: null,
                        receiveTotal: null,
                        finishRate: null,
                        status: null,
                        creator: null,
                        createTime: null,
                        updator: null,
                        updateTime: null,
                        id: null
                    };
                }
            }
        })
        .state('contract-info.edit',{
        	parent:'contract-info',
        	url:'/{id}/edit',
        	data:{
        		authorities: ['ROLE_USER']
        	},
        	views:{
        		'content@':{
        			templateUrl: 'app/contract/contract-info/contract-info-dialog.html',
        			controller: 'ContractInfoDialogController',
        			controllerAs: 'vm',
        		}
        	},
        	resolve:{
        		 translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                     $translatePartialLoader.addPart('contractInfo');
                     return $translate.refresh();
                 }],
                 entity: ['$stateParams', 'ContractInfo', function($stateParams, ContractInfo) {
                     return ContractInfo.get({id : $stateParams.id}).$promise;
                 }]
        	}
        })
        .state('contract-info.delete', {
            parent: 'contract-info',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/contract/contract-info/contract-info-delete-dialog.html',
                    controller: 'ContractInfoDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['ContractInfo', function(ContractInfo) {
                            return ContractInfo.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('contract-info', null, { reload: 'contract-info' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('contract-info.query', {
            parent: 'contract-info',
            url: '/queryDept?selectType&showChild',
            data: {
                authorities: ['ROLE_USER']
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
        });
        
    }

})();
