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
            url: '/contract-budget?page&sort&search',
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
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
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
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/contract/contract-budget/contract-budget-dialog.html',
                    controller: 'ContractBudgetDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['ContractBudget', function(ContractBudget) {
                            return ContractBudget.get({id : $stateParams.id}).$promise;
                        }]
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
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/contract/contract-budget/contract-budget-dialog.html',
                    controller: 'ContractBudgetDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                contractId: null,
                                type: null,
                                userId: null,
                                userName: null,
                                dept: null,
                                purchaseType: null,
                                budgetTotal: null,
                                status: null,
                                creator: null,
                                createTime: null,
                                updator: null,
                                updateTime: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('contract-budget', null, { reload: 'contract-budget' });
                }, function() {
                    $state.go('contract-budget');
                });
            }]
        })
        .state('contract-budget.edit', {
            parent: 'contract-budget',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/contract/contract-budget/contract-budget-dialog.html',
                    controller: 'ContractBudgetDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
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
