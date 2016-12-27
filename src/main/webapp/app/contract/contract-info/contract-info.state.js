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
            url: '/contract-info?page&sort&search',
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
        .state('contract-info-detail.edit', {
            parent: 'contract-info-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/contract/contract-info/contract-info-dialog.html',
                    controller: 'ContractInfoDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['ContractInfo', function(ContractInfo) {
                            return ContractInfo.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('contract-info.new', {
            parent: 'contract-info',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/contract/contract-info/contract-info-dialog.html',
                    controller: 'ContractInfoDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
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
                }).result.then(function() {
                    $state.go('contract-info', null, { reload: 'contract-info' });
                }, function() {
                    $state.go('contract-info');
                });
            }]
        })
        .state('contract-info.edit', {
            parent: 'contract-info',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/contract/contract-info/contract-info-dialog.html',
                    controller: 'ContractInfoDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
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
        });
    }

})();
