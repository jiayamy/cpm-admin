(function() {
    'use strict';

    angular
        .module('cpmApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('contract-receive', {
            parent: 'contract',
            url: '/contract-receive?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'cpmApp.contractReceive.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/contract/contract-receive/contract-receives.html',
                    controller: 'ContractReceiveController',
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
                    $translatePartialLoader.addPart('contractReceive');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('contract-receive-detail', {
            parent: 'contract',
            url: '/contract-receive/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'cpmApp.contractReceive.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/contract/contract-receive/contract-receive-detail.html',
                    controller: 'ContractReceiveDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('contractReceive');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'ContractReceive', function($stateParams, ContractReceive) {
                    return ContractReceive.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'contract-receive',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('contract-receive-detail.edit', {
            parent: 'contract-receive-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/contract/contract-receive/contract-receive-dialog.html',
                    controller: 'ContractReceiveDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['ContractReceive', function(ContractReceive) {
                            return ContractReceive.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('contract-receive.new', {
            parent: 'contract-receive',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/contract/contract-receive/contract-receive-dialog.html',
                    controller: 'ContractReceiveDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                contractId: null,
                                receiveTotal: null,
                                receiveDay: null,
                                status: null,
                                creator: null,
                                createTime: null,
                                updator: null,
                                updateTime: null,
                                receiver: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('contract-receive', null, { reload: 'contract-receive' });
                }, function() {
                    $state.go('contract-receive');
                });
            }]
        })
        .state('contract-receive.edit', {
            parent: 'contract-receive',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/contract/contract-receive/contract-receive-dialog.html',
                    controller: 'ContractReceiveDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['ContractReceive', function(ContractReceive) {
                            return ContractReceive.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('contract-receive', null, { reload: 'contract-receive' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('contract-receive.delete', {
            parent: 'contract-receive',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/contract/contract-receive/contract-receive-delete-dialog.html',
                    controller: 'ContractReceiveDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['ContractReceive', function(ContractReceive) {
                            return ContractReceive.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('contract-receive', null, { reload: 'contract-receive' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
