(function() {
    'use strict';

    angular
        .module('cpmApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('sale-weekly-stat', {
            parent: 'stat',
            url: '/sale-weekly-stat?page&deptId&deptName',
            data: {
                authorities: ['ROLE_STAT_CONTRACT'],
                pageTitle: 'cpmApp.saleWeeklyStat.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/stat/sale-weekly-stat/sale-weekly-stats.html',
                    controller: 'SaleWeeklyStatController',
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
                deptId : null,
                deptName: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        deptId: $stateParams.deptId,
                        deptName: $stateParams.deptName
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('saleWeeklyStat');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('sale-weekly-stat.queryDept', {
            parent: 'sale-weekly-stat',
            url: '/queryDept?selectType&showChild&showUser',
            data: {
                authorities: ['ROLE_STAT_CONTRACT']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/info/dept-info/dept-info-query.html',
                    controller: 'DeptInfoQueryController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function() {
                            return {
                            	selectType : $stateParams.selectType,
                            	showChild : $stateParams.showChild,
                            	showUser : $stateParams.showUser
                            }
                        }
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
    }
})();
