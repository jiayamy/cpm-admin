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
            url: '/product-price?page&sort&search',
            data: {
                authorities: ['ROLE_USER'],
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
                    value: 'id,asc',
                    squash: true
                },
                search: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
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
            parent: 'contract',
            url: '/product-price/{id}',
            data: {
                authorities: ['ROLE_USER'],
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
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('productPrice');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'ProductPrice', function($stateParams, ProductPrice) {
                    return ProductPrice.get({id : $stateParams.id}).$promise;
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
        .state('product-price-detail.edit', {
            parent: 'product-price-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/contract/product-price/product-price-dialog.html',
                    controller: 'ProductPriceDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['ProductPrice', function(ProductPrice) {
                            return ProductPrice.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('product-price.new', {
            parent: 'product-price',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/contract/product-price/product-price-dialog.html',
                    controller: 'ProductPriceDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                name: null,
                                type: null,
                                units: null,
                                price: null,
                                source: null,
                                creator: null,
                                createTime: null,
                                updator: null,
                                updateTime: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('product-price', null, { reload: 'product-price' });
                }, function() {
                    $state.go('product-price');
                });
            }]
        })
        .state('product-price.edit', {
            parent: 'product-price',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/contract/product-price/product-price-dialog.html',
                    controller: 'ProductPriceDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
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
        })
        .state('product-price.delete', {
            parent: 'product-price',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/contract/product-price/product-price-delete-dialog.html',
                    controller: 'ProductPriceDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
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
