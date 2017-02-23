(function() {
    'use strict';

    angular
        .module('cpmApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('overall-bonus', {
            parent: 'stat',
            url: '/overall-bonus?&statWeek&contractId',
            data: {
                pageTitle: 'cpmApp.overallBonus.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/stat/overall-bonus/overall-bonus.html',
                    controller: 'OverallBonusController',
                    controllerAs: 'vm'
                }
            },
            params: {
                statWeek: null,
                contractId: null
            },
            resolve: {
                pagingParams: ['$stateParams',function ($stateParams) {
                    return {
                        statWeek: $stateParams.statWeek,
                        contractId: $stateParams.contractId
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('overallBonus');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('overall-bonus-detail', {
            parent: 'overall-bonus',
            url: '/detail/{id}',
            data: {
                authorities: ['ROLE_PROJECT_USER'],
                pageTitle: 'cpmApp.projectUser.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/stat/overall-bonus/overall-bonus-detail.html',
                    controller: 'OverallBonusDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('overallBonus');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'OverallBonus', function($stateParams, OverallBonus) {
                    return OverallBonus.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'overall-bonus-controller',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
    }
})();
