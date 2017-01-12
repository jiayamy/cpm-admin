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
            url: '/contract-weekly-stat?page&sort&fromDate&toDate&statDate',
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
        });
    }

})();
