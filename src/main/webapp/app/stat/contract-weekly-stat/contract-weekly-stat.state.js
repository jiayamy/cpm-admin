(function() {
    'use strict';

    angular
        .module('cpmApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('contract-weekly-stat', {
            parent: 'stat',
            url: '/contract-weekly-stat?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'cpmApp.contractWeeklyStat.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/stat/contract-weekly-stat/contract-weekly-stats.html',
                    controller: 'ContractWeeklyStatController',
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
                    $translatePartialLoader.addPart('contractWeeklyStat');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('contract-weekly-stat-detail', {
            parent: 'stat',
            url: '/contract-weekly-stat/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'cpmApp.contractWeeklyStat.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/stat/contract-weekly-stat/contract-weekly-stat-detail.html',
                    controller: 'ContractWeeklyStatDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('contractWeeklyStat');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'ContractWeeklyStat', function($stateParams, ContractWeeklyStat) {
                    return ContractWeeklyStat.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'contract-weekly-stat',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('contract-weekly-stat-detail.edit', {
            parent: 'contract-weekly-stat-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/stat/contract-weekly-stat/contract-weekly-stat-dialog.html',
                    controller: 'ContractWeeklyStatDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['ContractWeeklyStat', function(ContractWeeklyStat) {
                            return ContractWeeklyStat.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('contract-weekly-stat.new', {
            parent: 'contract-weekly-stat',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/stat/contract-weekly-stat/contract-weekly-stat-dialog.html',
                    controller: 'ContractWeeklyStatDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                contractId: null,
                                receiveTotal: null,
                                costTotal: null,
                                grossProfit: null,
                                salesHumanCost: null,
                                salesPayment: null,
                                consultHumanCost: null,
                                consultPayment: null,
                                hardwarePurchase: null,
                                externalSoftware: null,
                                internalSoftware: null,
                                projectHumanCost: null,
                                projectPayment: null,
                                statWeek: null,
                                createTime: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('contract-weekly-stat', null, { reload: 'contract-weekly-stat' });
                }, function() {
                    $state.go('contract-weekly-stat');
                });
            }]
        })
        .state('contract-weekly-stat.edit', {
            parent: 'contract-weekly-stat',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/stat/contract-weekly-stat/contract-weekly-stat-dialog.html',
                    controller: 'ContractWeeklyStatDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['ContractWeeklyStat', function(ContractWeeklyStat) {
                            return ContractWeeklyStat.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('contract-weekly-stat', null, { reload: 'contract-weekly-stat' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('contract-weekly-stat.delete', {
            parent: 'contract-weekly-stat',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/stat/contract-weekly-stat/contract-weekly-stat-delete-dialog.html',
                    controller: 'ContractWeeklyStatDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['ContractWeeklyStat', function(ContractWeeklyStat) {
                            return ContractWeeklyStat.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('contract-weekly-stat', null, { reload: 'contract-weekly-stat' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
