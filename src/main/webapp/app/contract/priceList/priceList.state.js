(function() {
    'use strict';

    angular
        .module('cpmApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('priceList', {
            parent: 'contract',
            url: '/priceList',
            data: {
                authorities: ['ROLE_ADMIN'],
                pageTitle: 'priceList.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/contract/priceList/priceList.html',
                    controller: 'PriceListController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('priceList');
                    return $translate.refresh();
                }]
            }
        })
        .state('priceList-detail',{
        	parent: 'contract',
            url: '/priceList',
            data: {
                authorities: ['ROLE_ADMIN'],
                pageTitle: 'priceList.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/contract/priceList/priceList-detail.html',
                    controller: 'PriceListController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('priceList');
                    return $translate.refresh();
                }]
            }
        })
    }
})();
