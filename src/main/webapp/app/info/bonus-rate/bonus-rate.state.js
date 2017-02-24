(function() {
    'use strict';

    angular
        .module('cpmApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('bonus-rate', {
            parent: 'info',
            url: '/bonus-rate?page&sort&deptType&contractType',
            data: {
                authorities: ['ROLE_INFO_BASIC'],
                pageTitle: 'cpmApp.bonusRate.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/info/bonus-rate/bonus-rates.html',
                    controller: 'BonusRateController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'wbr.contractType,desc',
                    squash: true
                },
                deptType: null,
                contractType: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        deptType: $stateParams.deptType,
                        contractType: $stateParams.contractType
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('bonusRate');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        });
    }

})();
