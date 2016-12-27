(function() {
    'use strict';

    angular
        .module('cpmApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('user-timesheet', {
            parent: 'timesheet',
            url: '/user-timesheet?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'cpmApp.userTimesheet.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/timesheet/user-timesheet/user-timesheets.html',
                    controller: 'UserTimesheetController',
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
                    $translatePartialLoader.addPart('userTimesheet');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('user-timesheet-detail', {
            parent: 'timesheet',
            url: '/user-timesheet/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'cpmApp.userTimesheet.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/timesheet/user-timesheet/user-timesheet-detail.html',
                    controller: 'UserTimesheetDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('userTimesheet');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'UserTimesheet', function($stateParams, UserTimesheet) {
                    return UserTimesheet.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'user-timesheet',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('user-timesheet-detail.edit', {
            parent: 'user-timesheet-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/timesheet/user-timesheet/user-timesheet-dialog.html',
                    controller: 'UserTimesheetDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['UserTimesheet', function(UserTimesheet) {
                            return UserTimesheet.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('user-timesheet.new', {
            parent: 'user-timesheet',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/timesheet/user-timesheet/user-timesheet-dialog.html',
                    controller: 'UserTimesheetDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                workDay: null,
                                userId: null,
                                type: null,
                                objId: null,
                                objName: null,
                                realInput: null,
                                acceptInput: null,
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
                    $state.go('user-timesheet', null, { reload: 'user-timesheet' });
                }, function() {
                    $state.go('user-timesheet');
                });
            }]
        })
        .state('user-timesheet.edit', {
            parent: 'user-timesheet',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/timesheet/user-timesheet/user-timesheet-dialog.html',
                    controller: 'UserTimesheetDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['UserTimesheet', function(UserTimesheet) {
                            return UserTimesheet.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('user-timesheet', null, { reload: 'user-timesheet' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('user-timesheet.delete', {
            parent: 'user-timesheet',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/timesheet/user-timesheet/user-timesheet-delete-dialog.html',
                    controller: 'UserTimesheetDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['UserTimesheet', function(UserTimesheet) {
                            return UserTimesheet.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('user-timesheet', null, { reload: 'user-timesheet' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
