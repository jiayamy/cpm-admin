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
            url: '/contract-monthly-stat?page&sort&fromDate&toDate&statDate',
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
                    $translatePartialLoader.addPart('contractWeeklyStat');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'ContractMonthlyStat', function($stateParams, ContractMonthlyStat) {
                    return ContractMonthlyStat.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'contract-monthly-stat',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        });
    }

})();
