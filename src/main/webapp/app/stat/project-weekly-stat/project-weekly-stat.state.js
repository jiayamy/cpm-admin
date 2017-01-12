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
            url: '/project-weekly-stat?page&sort&fromDate&toDate&statDate',
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
                fromDate : null,
                toDate: null,
                statDate: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        fromDate: $stateParams.fromDate,
                        toDate: $stateParams.toDate,
                        statDate: $stateParams.statDate
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
        });
    }

})();
