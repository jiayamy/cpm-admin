(function() {
    'use strict';

    angular
        .module('cpmApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('project-timesheet', {
            parent: 'project',
            url: '/project-timesheet?page&sort&search',
            data: {
                authorities: ['ROLE_PROJECT_TIMESHEET'],
                pageTitle: 'cpmApp.projectTimesheet.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/project/project-timesheet/project-timesheets.html',
                    controller: 'ProjectTimesheetController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,desc',
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
                    $translatePartialLoader.addPart('projectTimesheet');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('project-timesheet-detail', {
            parent: 'project-timesheet',
            url: '/detail/{id}',
            data: {
                authorities: ['ROLE_PROJECT_TIMESHEET'],
                pageTitle: 'cpmApp.projectTimesheet.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/project/project-timesheet/project-timesheet-detail.html',
                    controller: 'ProjectTimesheetDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('projectTimesheet');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'ProjectTimesheet', function($stateParams, ProjectTimesheet) {
                    return ProjectTimesheet.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'project-timesheet',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('project-timesheet-detail.edit', {
            parent: 'project-timesheet-detail',
            url: '/edit',
            data: {
                authorities: ['ROLE_PROJECT_TIMESHEET']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/project/project-timesheet/project-timesheet-dialog.html',
                    controller: 'ProjectTimesheetDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['ProjectTimesheet', function(ProjectTimesheet) {
                            return ProjectTimesheet.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('project-timesheet.new', {
            parent: 'project-timesheet',
            url: '/new',
            data: {
                authorities: ['ROLE_PROJECT_TIMESHEET']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/project/project-timesheet/project-timesheet-dialog.html',
                    controller: 'ProjectTimesheetDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                projectId: null,
                                userId: null,
                                realInput: null,
                                acceptInput: null,
                                workDay: null,
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
                    $state.go('project-timesheet', null, { reload: 'project-timesheet' });
                }, function() {
                    $state.go('project-timesheet');
                });
            }]
        })
        .state('project-timesheet.edit', {
            parent: 'project-timesheet',
            url: '/edit/{id}',
            data: {
                authorities: ['ROLE_PROJECT_TIMESHEET']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/project/project-timesheet/project-timesheet-dialog.html',
                    controller: 'ProjectTimesheetDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['ProjectTimesheet', function(ProjectTimesheet) {
                            return ProjectTimesheet.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('project-timesheet', null, { reload: 'project-timesheet' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('project-timesheet.delete', {
            parent: 'project-timesheet',
            url: '/delete/{id}',
            data: {
                authorities: ['ROLE_PROJECT_TIMESHEET']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/project/project-timesheet/project-timesheet-delete-dialog.html',
                    controller: 'ProjectTimesheetDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['ProjectTimesheet', function(ProjectTimesheet) {
                            return ProjectTimesheet.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('project-timesheet', null, { reload: 'project-timesheet' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
