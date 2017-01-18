(function() {
    'use strict';

    angular
        .module('cpmApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('contract-cost', {
            parent: 'contract',
            url: '/contract-cost?page&sort&contractId&type&name',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'cpmApp.contractCost.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/contract/contract-cost/contract-costs.html',
                    controller: 'ContractCostController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'wcc.id,asc',
                    squash: true
                },
                contractId: null,
                type: null,
                name: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        contractId: $stateParams.contractId,
                        type: $stateParams.type,
                        name: $stateParams.name
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('contractCost');
                    $translatePartialLoader.addPart('contractInfo');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('contract-cost-detail', {
            parent: 'contract',
            url: '/contract-cost/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'cpmApp.contractCost.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/contract/contract-cost/contract-cost-detail.html',
                    controller: 'ContractCostDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('contractCost');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'ContractCost', function($stateParams, ContractCost) {
                    return ContractCost.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'contract-cost',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        
        .state('contract-cost-detail.edit',{
        	parent: 'contract-cost',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            views:{
            	'content@':{
            		templateUrl: 'app/contract/contract-cost/contract-cost-dialog.html',
                    controller: 'ContractCostDialogController',
                    controllerAs: 'vm'
            	}
            },
            resolve:{
            	translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('contractCost');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'ContractCost', function($stateParams, ContractCost) {
                    return ContractCost.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('contract-cost.new',{
        	parent: 'contract-cost',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            views:{
            	'content@':{
	        		 templateUrl: 'app/contract/contract-cost/contract-cost-dialog.html',
	                 controller: 'ContractCostDialogController',
	                 controllerAs: 'vm'
            	}
            },
        	resolve:{
        		 translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                     $translatePartialLoader.addPart('contractCost');
                     return $translate.refresh();
                 }],
                 entity: function () {
                     return {
                         contractId: null,
                         budgetId: null,
                         deptId: null,
                         dept: null,
                         name: null,
                         type: null,
                         total: null,
                         costDesc: null,
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
        .state('contract-cost.edit',{
        	parent: 'contract-cost',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            views:{
            	'content@':{
            		templateUrl: 'app/contract/contract-cost/contract-cost-dialog.html',
                    controller: 'ContractCostDialogController',
                    controllerAs: 'vm'
            	}
            },
            resolve:{
            	translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('contractCost');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'ContractCost', function($stateParams, ContractCost) {
                    return ContractCost.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('contract-cost.delete', {
            parent: 'contract-cost',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/contract/contract-cost/contract-cost-delete-dialog.html',
                    controller: 'ContractCostDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['ContractCost', function(ContractCost) {
                            return ContractCost.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('contract-cost', null, { reload: 'contract-cost' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
