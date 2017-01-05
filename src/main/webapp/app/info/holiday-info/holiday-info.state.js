(function() {
    'use strict';

    angular
        .module('cpmApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('holiday-info', {
            parent: 'info',
            url: '/holiday-info?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'cpmApp.holidayInfo.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/info/holiday-info/holiday-infos.html',
                    controller: 'HolidayInfoController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'currDay,desc',
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
                    $translatePartialLoader.addPart('holidayInfo');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('holiday-info-detail', {
            parent: 'info',
            url: '/holiday-info/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'cpmApp.holidayInfo.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/info/holiday-info/holiday-info-detail.html',
                    controller: 'HolidayInfoDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('holidayInfo');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'HolidayInfo', function($stateParams, HolidayInfo) {
                    return HolidayInfo.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'holiday-info',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('holiday-info-detail.edit', {
            parent: 'holiday-info-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/info/holiday-info/holiday-info-dialog.html',
                    controller: 'HolidayInfoDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['HolidayInfo', function(HolidayInfo) {
                            return HolidayInfo.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('holiday-info.new', {
            parent: 'holiday-info',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/info/holiday-info/holiday-info-dialog.html',
                    controller: 'HolidayInfoDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                currDay: null,
                                type: null,
                                creator: null,
                                createTime: null,
                                updator: null,
                                updateTime: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('holiday-info', null, { reload: 'holiday-info' });
                }, function() {
                    $state.go('holiday-info');
                });
            }]
        })
        .state('holiday-info.edit', {
            parent: 'holiday-info',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/info/holiday-info/holiday-info-dialog.html',
                    controller: 'HolidayInfoDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['HolidayInfo', function(HolidayInfo) {
                            return HolidayInfo.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('holiday-info', null, { reload: 'holiday-info' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('holiday-info.delete', {
            parent: 'holiday-info',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/info/holiday-info/holiday-info-delete-dialog.html',
                    controller: 'HolidayInfoDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['HolidayInfo', function(HolidayInfo) {
                            return HolidayInfo.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('holiday-info', null, { reload: 'holiday-info' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
