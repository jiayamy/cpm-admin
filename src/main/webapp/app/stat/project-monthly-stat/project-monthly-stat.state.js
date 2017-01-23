(function() {
    'use strict';

    angular
        .module('cpmApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('project-monthly-stat', {
            parent: 'stat',
            url: '/project-monthly-stat?page&projectId',
            data: {
                authorities: ['ROLE_STAT_PROJECT'],
                pageTitle: 'cpmApp.projectMonthlyStat.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/stat/project-monthly-stat/project-monthly-stats.html',
                    controller: 'ProjectMonthlyStatController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'm.id,desc',
                    squash: true
                },
                projectId: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        projectId: $stateParams.projectId
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('projectMonthlyStat');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('project-monthly-stat-detail', {
            parent: 'project-monthly-stat',
            url: '/detail/{id}',
            data: {
                authorities: ['ROLE_STAT_PROJECT'],
                pageTitle: 'cpmApp.projectMonthlyStat.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/stat/project-monthly-stat/project-monthly-stat-detail.html',
                    controller: 'ProjectMonthlyStatDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                	$translatePartialLoader.addPart('projectMonthlyStat');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'ProjectMonthlyStat', function($stateParams, ProjectMonthlyStat) {
                    return ProjectMonthlyStat.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                	var currentStateData = {
                            name: 'project-monthly-stat',
                            params: {
                                page: {
                                    value: '1',
                                    squash: true
                                },
                                sort: {
                                    value: 'm.id,desc',
                                    squash: true
                                },
                                projectId: null
                            },
                            url: $state.href($state.current.name, {
                                page: {
                                    value: '1',
                                    squash: true
                                },
                                sort: {
                                    value: 'm.id,desc',
                                    squash: true
                                },
                                projectId: null
                            })
                        };
                    return currentStateData;
                }]
            }
        })
        .state('project-monthly-stat-detail.chart', {
            parent: 'project-monthly-stat',
            url: '/chart/{id}/queryChart?fromDate&toDate',
            data: {
                authorities: ['ROLE_STAT_PROJECT'],
                pageTitle: 'cpmApp.projectInfo.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/stat/project-monthly-stat/project-monthly-stat-chart.html',
                    controller: 'ProjectMonthlyStatChartController',
                    controllerAs: 'vm'
                }
            },
            params: {
                fromDate: null,
                toDate : null,
                id : null
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                	$translatePartialLoader.addPart('projectMonthlyStat');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        fromDate: $stateParams.fromDate,
                        toDate : $stateParams.toDate,
                        id : $stateParams.id
                    };
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: 'project-monthly-stat',
                        params: {
                            page: {
                                value: '1',
                                squash: true
                            },
                            sort: {
                                value: 'm.id,desc',
                                squash: true
                            },
                            projectId: null
                        },
                        url: $state.href('project-monthly-stat', {
                            page: {
                                value: '1',
                                squash: true
                            },
                            sort: {
                                value: 'm.id,desc',
                                squash: true
                            },
                            projectId: null
                        })
                    };
                    return currentStateData;
                }]
                
            }
        });
    }

})();
