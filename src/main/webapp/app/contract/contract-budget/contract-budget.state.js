(function() {
    'use strict';

    angular
        .module('cpmApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('contract-budget', {
            parent: 'contract',
            url: '/contract-budget?page&sort&serialNum&name&budgetName',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'cpmApp.contractBudget.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/contract/contract-budget/contract-budgets.html',
                    controller: 'ContractBudgetController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'cb.id,asc',
                    squash: true
                },
                serialNum: null,
                name: null,
                budgetName: null
            },
            resolve: {
                pagingParams: ["$state",'$stateParams', 'PaginationUtil', function ($state,$stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        serialNum: $stateParams.serialNum,
                        name: $stateParams.name,
                        budgetName: $stateParams.budgetName
                        
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('contractBudget');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('contract-budget-detail', {
            parent: 'contract',
            url: '/contract-budget/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'cpmApp.contractBudget.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/contract/contract-budget/contract-budget-detail.html',
                    controller: 'ContractBudgetDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('contractBudget');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'ContractBudget', function($stateParams, ContractBudget) {
                    return ContractBudget.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'contract-budget',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('contract-budget-detail.edit', {
            parent: 'contract-budget-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            views: {
            	'content@': {
            		 templateUrl: 'app/contract/contract-budget/contract-budget-dialog.html',
                     controller: 'ContractBudgetDialogController',
                     controllerAs: 'vm'
            	}
            },
            resolve: {
            	translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('contractBudget');
                    $translatePartialLoader.addPart('deptInfo');
                    return $translate.refresh();
                }],
            	entity: ['ContractBudget','$stateParams', function(ContractBudget,$stateParams) {
            		return ContractBudget.get({id : $stateParams.id}).$promise;
            	}],
            	 previousState: ["$state", function ($state) {
                     var currentStateData = {
                     	queryDept:'contract-budget-detail.edit.queryDept',
                         name: $state.current.name || 'contract-budget-detail',
                         params: $state.params,
                         url: $state.href($state.current.name, $state.params)
                     };
                     return currentStateData;
            	 }]
            }
        })
        .state('contract-budget-detail.edit.queryDept', {
            parent: 'contract-budget-detail.edit',
            url: '/queryDept?selectType&showChild',
            data: {
                authorities: ['ROLE_PROJECT_INFO']
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
        .state('contract-budget.new', {
            parent: 'contract-budget',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            views: {
            	'content@': {
            		templateUrl: 'app/contract/contract-budget/contract-budget-dialog.html',
            		controller: 'ContractBudgetDialogController',
            		controllerAs: 'vm'
            	}
            },
           	resolve: {
           		translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('contractBudget');
                    $translatePartialLoader.addPart('deptInfo');
                    return $translate.refresh();
                }],
                entity: function () {
                    return {
                    	serialNum: null,
                    	name: null,
                    	budgetName: null,
                        userName: null,
                        dept: null,
                        purchaseType: null,
                        budgetTotal: null,
                        id: null
                    };
                },
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                    	queryDept:'contract-budget.new.queryDept',
                        name: $state.current.name || 'contract-budget',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('contract-budget.new.queryDept', {
            parent: 'contract-budget.new',
            url: '/queryDept?selectType&showChild',
            data: {
                authorities: ['ROLE_PROJECT_INFO']
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
        .state('contract-budget.edit', {
            parent: 'contract-budget',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            views: {
            	'content@': {
                    templateUrl: 'app/contract/contract-budget/contract-budget-dialog.html',
                    controller: 'ContractBudgetDialogController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            	translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
	                $translatePartialLoader.addPart('contractBudget');
	                $translatePartialLoader.addPart('deptInfo');
	                return $translate.refresh();
                }],
                entity: ['$stateParams','ContractBudget', function($stateParams,ContractBudget) {
                    return ContractBudget.get({id : $stateParams.id}).$promise;
                 }],
                 previousState: ["$state", function ($state) {
 	                var currentStateData = {
 	                	queryDept:'contract-budget.edit.queryDept',
 	                    name: $state.current.name || 'contract-budget',
 	                    params: $state.params,
 	                    url: $state.href($state.current.name, $state.params)
 	                };
 	                return currentStateData;
 	            }]
             }
        })
        .state('contract-budget.edit.queryDept', {
            parent: 'contract-budget.edit',
            url: '/queryDept?selectType&showChild',
            data: {
                authorities: ['ROLE_PROJECT_INFO']
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
        .state('contract-budget.delete', {
            parent: 'contract-budget',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/contract/contract-budget/contract-budget-delete-dialog.html',
                    controller: 'ContractBudgetDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['ContractBudget', function(ContractBudget) {
                            return ContractBudget.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('contract-budget', null, { reload: 'contract-budget' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
