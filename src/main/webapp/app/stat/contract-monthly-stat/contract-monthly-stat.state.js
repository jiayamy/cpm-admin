(function() {
    'use strict';

    angular
        .module('cpmApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('contract-monthly-stat', {
            parent: 'stat',
            url: '/contract-monthly-stat?page&contractId',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'cpmApp.contractMonthlyStat.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/stat/contract-monthly-stat/contract-monthly-stats.html',
                    controller: 'ContractMonthlyStatController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'm.id,asc',
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
                    $translatePartialLoader.addPart('contractMonthlyStat');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('contract-monthly-stat-detail', {
            parent: 'stat',
            url: '/contract-monthly-stat/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'cpmApp.contractMonthlyStat.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/stat/contract-monthly-stat/contract-monthly-stat-detail.html',
                    controller: 'ContractMonthlyStatDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                	$translatePartialLoader.addPart('contractMonthlyStat');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'ContractMonthlyStat', function($stateParams, ContractMonthlyStat) {
                    return ContractMonthlyStat.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                	var currentStateData = {
                            name: 'contract-monthly-stat',
                            params: {
                                page: {
                                    value: '1',
                                    squash: true
                                },
                                sort: {
                                    value: 'm.id,asc',
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
                                    value: 'm.id,asc',
                                    squash: true
                                },
                                contractId: null
                            })
                        };
                    return currentStateData;
                }]
            }
        }).state('contract-monthly-stat-detail.chart', {
            parent: 'stat',
            url: '/contract-monthly-stat/{id}/queryChart?fromDate&toDate&contractId',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'cpmApp.contractInfo.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/stat/contract-monthly-stat/contract-monthly-stat-chart.html',
                    controller: 'ContractMonthlyStatChartController',
                    controllerAs: 'vm'
                }
            },
            params: {
                fromDate: null,
                toDate : null
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                	$translatePartialLoader.addPart('contractMonthlyStat');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        fromDate: $stateParams.fromDate,
                        toDate : $stateParams.toDate,
                        contractId : $stateParams.id
                    };
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: 'contract-monthly-stat',
                        params: {
                            page: {
                                value: '1',
                                squash: true
                            },
                            sort: {
                                value: 'm.id,asc',
                                squash: true
                            },
                            contractId: null
                        },
                        url: $state.href('contract-monthly-stat', {
                            page: {
                                value: '1',
                                squash: true
                            },
                            sort: {
                                value: 'm.id,asc',
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
