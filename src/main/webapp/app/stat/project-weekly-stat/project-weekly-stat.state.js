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
            url: '/project-weekly-stat?page&projectId',
            data: {
                authorities: ['ROLE_STAT_PROJECT'],
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
                    value: 'm.id,desc',
                    squash: true
                },
                projectId: null
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    return $ocLazyLoad.load([
                                             'app/stat/project-weekly-stat/project-weekly-stat.service.js',
                                             'app/stat/project-weekly-stat/project-weekly-stat.controller.js',
                                             'app/project/project-info/project-info.service.js']);
                }],
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
                    $translatePartialLoader.addPart('projectWeeklyStat');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('project-weekly-stat-detail', {
            parent: 'project-weekly-stat',
            url: '/detail/{id}',
            data: {
                authorities: ['ROLE_STAT_PROJECT'],
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
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    return $ocLazyLoad.load('app/stat/project-weekly-stat/project-weekly-stat-detail.controller.js');
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('projectWeeklyStat');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', '$ocLazyLoad','$injector', function($stateParams, $ocLazyLoad,$injector) {
                	return $ocLazyLoad.load('app/stat/project-weekly-stat/project-weekly-stat.service.js').then(
                			function(){
                				return $injector.get('ProjectWeeklyStat').get({id : $stateParams.id}).$promise;
                			}
                	);
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
        .state('project-weekly-stat-detail.chart', {
            parent: 'project-weekly-stat',
            url: '/chart/{id}/queryChart?fromDate&toDate',
            data: {
                authorities: ['ROLE_STAT_PROJECT'],
                pageTitle: 'cpmApp.projectInfo.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/stat/project-weekly-stat/project-weekly-stat-chart.html',
                    controller: 'ProjectWeeklyStatChartController',
                    controllerAs: 'vm'
                }
            },
            params: {
                fromDate: null,
                toDate : null,
                id : null
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    return $ocLazyLoad.load(['app/stat/project-weekly-stat/project-weekly-stat-chart.controller.js',
                                             'app/stat/project-weekly-stat/project-weekly-stat-chart.service.js']);
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                	$translatePartialLoader.addPart('projectWeeklyStat');
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
                        name: 'project-weekly-stat',
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
                        url: $state.href('project-weekly-stat', {
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
