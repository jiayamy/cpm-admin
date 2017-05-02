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
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    return $ocLazyLoad.load([
                                             'app/stat/bonus/bonus.service.js',
                                             'app/stat/bonus/bonus.controller.js',
                                             'app/contract/contract-info/contract-info.service.js']);
                }],
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
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    return $ocLazyLoad.load('app/stat/bonus/bonus-detail.controller.js');
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('bonus');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', '$ocLazyLoad','$injector', function($stateParams, $ocLazyLoad,$injector) {
                	return $ocLazyLoad.load('app/stat/bonus/bonus.service.js').then(
                			function(){
                				return $injector.get('Bonus').get({id : $stateParams.id}).$promise;
                			}
                	);
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
