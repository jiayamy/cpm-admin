(function() {
    'use strict';

    angular
        .module('cpmApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('product-price', {
            parent: 'contract',
            url: '/product-price?page&sort&type&source&name',
            data: {
                authorities: ['ROLE_CONTRACT_PRODUCTPRICE'],
                pageTitle: 'cpmApp.productPrice.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/contract/product-price/product-prices.html',
                    controller: 'ProductPriceController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'wpp.id,asc',
                    squash: true
                },
                type: null,
                source: null,
                name: null
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    return $ocLazyLoad.load([
                                             'app/contract/product-price/product-price.service.js',
                                             'app/contract/product-price/product-price.controller.js']);
                }],
                pagingParams: ["$state",'$stateParams', 'PaginationUtil', function ($state,$stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        type: $stateParams.type,
                        source: $stateParams.source,
                        name: $stateParams.name
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('productPrice');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('product-price-detail', {
            parent: 'product-price',
            url: '/detail/{id}',
            data: {
                authorities: ['ROLE_CONTRACT_PRODUCTPRICE'],
                pageTitle: 'cpmApp.productPrice.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/contract/product-price/product-price-detail.html',
                    controller: 'ProductPriceDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
            		return $ocLazyLoad.load('app/contract/product-price/product-price-detail.controller.js');
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('productPrice');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', '$ocLazyLoad','$injector', function($stateParams, $ocLazyLoad,$injector) {
                	return $ocLazyLoad.load('app/contract/product-price/product-price.service.js').then(
                			function(){
                				return $injector.get('ProductPrice').get({id : $stateParams.id}).$promise;
                			}
                	);
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'product-price',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('product-price.new', {
            parent: 'product-price',
            url: '/new',
            data: {
                authorities: ['ROLE_CONTRACT_PRODUCTPRICE']
            },
            views:{
            	'content@':{
            		templateUrl: 'app/contract/product-price/product-price-dialog.html',
            		controller: 'ProductPriceDialogController',
            		controllerAs: 'vm',
            	}
            },       
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
            		return $ocLazyLoad.load('app/contract/product-price/product-price-dialog.controller.js');
                }],
            	translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('productPrice');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
                entity: function () {
                    return {
                        name: null,
                        type: 1,
                        units: null,
                        price: null,
                        source: 1,
                        creator: null,
                        createTime: null,
                        updator: null,
                        updateTime: null,
                        id: null
                    };
                },
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'product-price',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('product-price.edit', {
            parent: 'product-price',
            url: '/edit/{id}',
            data: {
                authorities: ['ROLE_CONTRACT_PRODUCTPRICE']
            },
            views: {
            	'content@': {
            		templateUrl: 'app/contract/product-price/product-price-dialog.html',
            		controller: 'ProductPriceDialogController',
            		controllerAs: 'vm',
            	}
            },  
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
            		return $ocLazyLoad.load('app/contract/product-price/product-price-dialog.controller.js');
                }],
            	translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
            		$translatePartialLoader.addPart('productPrice');
            		$translatePartialLoader.addPart('global');
	                return $translate.refresh();
            	}],
            	entity: ['$stateParams', '$ocLazyLoad','$injector', function($stateParams, $ocLazyLoad,$injector) {
                	return $ocLazyLoad.load('app/contract/product-price/product-price.service.js').then(
                			function(){
                				return $injector.get('ProductPrice').get({id : $stateParams.id}).$promise;
                			}
                	);
                }],
                previousState: ["$state", function ($state) {
 	                var currentStateData = {
 	                    name: $state.current.name || 'product-price',
 	                    params: $state.params,
 	                    url: $state.href($state.current.name, $state.params)
 	                };
 	                return currentStateData;
                }]
            }
        })
        .state('product-price.delete', {
            parent: 'product-price',
            url: '/delete/{id}',
            data: {
                authorities: ['ROLE_CONTRACT_PRODUCTPRICE']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/contract/product-price/product-price-delete-dialog.html',
                    controller: 'ProductPriceDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                    	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    		return $ocLazyLoad.load('app/contract/product-price/product-price-delete-dialog.controller.js');
                        }],
                        entity: ['ProductPrice', function(ProductPrice) {
                            return ProductPrice.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('product-price', null, { reload: 'product-price' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
