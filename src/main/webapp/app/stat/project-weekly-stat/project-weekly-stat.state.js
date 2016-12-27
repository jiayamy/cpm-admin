(function() {
    'use strict';

    angular
        .module('cpmApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('project-weekly-stat', {
            parent: 'stat',
            url: '/project-weekly-stat?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'cpmApp.projectWeeklyStat.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/stat/project-weekly-stat/project-weekly-stats.html',
                    controller: 'ProjectWeeklyStatController',
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
                    $translatePartialLoader.addPart('projectWeeklyStat');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('project-weekly-stat-detail', {
            parent: 'stat',
            url: '/project-weekly-stat/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'cpmApp.projectWeeklyStat.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/stat/project-weekly-stat/project-weekly-stat-detail.html',
                    controller: 'ProjectWeeklyStatDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('projectWeeklyStat');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'ProjectWeeklyStat', function($stateParams, ProjectWeeklyStat) {
                    return ProjectWeeklyStat.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'project-weekly-stat',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('project-weekly-stat-detail.edit', {
            parent: 'project-weekly-stat-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/stat/project-weekly-stat/project-weekly-stat-dialog.html',
                    controller: 'ProjectWeeklyStatDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['ProjectWeeklyStat', function(ProjectWeeklyStat) {
                            return ProjectWeeklyStat.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('project-weekly-stat.new', {
            parent: 'project-weekly-stat',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/stat/project-weekly-stat/project-weekly-stat-dialog.html',
                    controller: 'ProjectWeeklyStatDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                projectId: null,
                                humanCost: null,
                                payment: null,
                                statWeek: null,
                                createTime: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('project-weekly-stat', null, { reload: 'project-weekly-stat' });
                }, function() {
                    $state.go('project-weekly-stat');
                });
            }]
        })
        .state('project-weekly-stat.edit', {
            parent: 'project-weekly-stat',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/stat/project-weekly-stat/project-weekly-stat-dialog.html',
                    controller: 'ProjectWeeklyStatDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['ProjectWeeklyStat', function(ProjectWeeklyStat) {
                            return ProjectWeeklyStat.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('project-weekly-stat', null, { reload: 'project-weekly-stat' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('project-weekly-stat.delete', {
            parent: 'project-weekly-stat',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/stat/project-weekly-stat/project-weekly-stat-delete-dialog.html',
                    controller: 'ProjectWeeklyStatDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['ProjectWeeklyStat', function(ProjectWeeklyStat) {
                            return ProjectWeeklyStat.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('project-weekly-stat', null, { reload: 'project-weekly-stat' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
