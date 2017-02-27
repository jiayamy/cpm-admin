(function() {
    'use strict';

    angular
        .module('cpmApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('sales-bonus', {
            parent: 'stat',
            url: '/sales-bonus?originYear&statWeek&contractId&salesManId&salesMan',
            data: {
                authorities: ['ROLE_STAT_SALES_BONUS'],
                pageTitle: 'cpmApp.salesBonus.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/stat/sales-bonus/sales-bonuss.html',
                    controller: 'SalesBonusController',
                    controllerAs: 'vm'
                }
            },
            params: {
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                    	originYear: $stateParams.originYear,
                    	statWeek: $stateParams.statWeek,
                    	contractId: $stateParams.contractId,
                    	salesManId: $stateParams.salesManId,
                    	salesMan: $stateParams.salesMan
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('salesBonus');
                    $translatePartialLoader.addPart('deptInfo');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('sales-bonus.queryDept', {
            parent: 'sales-bonus',
            url: '/queryDept?selectType&showChild',
            data: {
                authorities: ['ROLE_STAT_SALES_BONUS']
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
                            	showChild : $stateParams.showChild
                            }
                        }
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
