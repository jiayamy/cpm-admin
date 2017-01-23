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
            url: '/contract-weekly-stat?page&contractId',
            data: {
                authorities: ['ROLE_STAT_CONTRACT'],
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
                    value: 'm.id,desc',
                    squash: true
                },
                contractId : null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        contractId: $stateParams.contractId
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
            parent: 'contract-weekly-stat',
            url: '/detail/{id}',
            data: {
                authorities: ['ROLE_STAT_CONTRACT'],
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
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'ContractWeeklyStat', function($stateParams, ContractWeeklyStat) {
                    return ContractWeeklyStat.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                	var currentStateData = {
                            name: 'contract-weekly-stat',
                            params: {
                                page: {
                                    value: '1',
                                    squash: true
                                },
                                sort: {
                                    value: 'm.id,desc',
                                    squash: true
                                },
                                contractId: null
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
                                contractId: null
                            })
                        };
                    return currentStateData;
                }]
            }
        }).state('contract-weekly-stat-detail.chart', {
            parent: 'contract-weekly-stat',
            url: '/chart/{id}/queryChart?fromDate&toDate',
            data: {
                authorities: ['ROLE_STAT_CONTRACT'],
                pageTitle: 'cpmApp.contractInfo.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/stat/contract-weekly-stat/contract-weekly-stat-chart.html',
                    controller: 'ContractWeeklyStatChartController',
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
                	$translatePartialLoader.addPart('contractWeeklyStat');
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
                        name: 'contract-weekly-stat',
                        params: {
                            page: {
                                value: '1',
                                squash: true
                            },
                            sort: {
                                value: 'm.id,desc',
                                squash: true
                            },
                            contractId: null
                        },
                        url: $state.href('contract-weekly-stat', {
                            page: {
                                value: '1',
                                squash: true
                            },
                            sort: {
                                value: 'm.id,desc',
                                squash: true
                            },
                            contractId: null
                        })
                    };
                    return currentStateData;
                }]
                
            }
        });
    }

})();
