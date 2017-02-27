(function() {
    'use strict';

    angular
        .module('cpmApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('bonus', {
            parent: 'stat',
            url: '/bonus?&statWeek&contractId',
            data: {
            	authorities: ['ROLE_STAT_BONUS'],
                pageTitle: 'cpmApp.bonus.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/stat/bonus/bonus.html',
                    controller: 'BonusController',
                    controllerAs: 'vm'
                }
            },
            params: {
            	page: {
                    value: '1',
                    squash: true
                },
                statWeek: null,
                contractId: null
            },
            resolve: {
                pagingParams: ['$stateParams','PaginationUtil',function ($stateParams,PaginationUtil) {
                    return {
                    	page: PaginationUtil.parsePage($stateParams.page),
                        statWeek: $stateParams.statWeek,
                        contractId: $stateParams.contractId
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('bonus');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('bonus-detail', {
            parent: 'bonus',
            url: '/detail/{id}',
            data: {
                authorities: ['ROLE_STAT_BONUS'],
                pageTitle: 'cpmApp.bonus.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/stat/bonus/bonus-detail.html',
                    controller: 'BonusDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('bonus');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Bonus', function($stateParams, Bonus) {
                    return Bonus.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'bonus',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
    }
})();
