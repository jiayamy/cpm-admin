(function() {
    'use strict';

    angular
        .module('cpmApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('purchase-item', {
            parent: 'contract',
            url: '/purchase-item?name&contractId&source&type',
            data: {
                authorities: ['ROLE_CONTRACT_PURCHASE'],
                pageTitle: 'cpmApp.purchaseItem.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/contract/purchase-item/purchase-items.html',
                    controller: 'PurchaseItemController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'wpi.id,desc',
                    squash: true
                }
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        name: $stateParams.name,
                        contractId: $stateParams.contractId,
                        source: $stateParams.source,
                        type: $stateParams.type
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('purchaseItem');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('purchase-item-detail', {
            parent: 'purchase-item',
            url: '/detail/{id}',
            data: {
                authorities: ['ROLE_CONTRACT_PURCHASE'],
                pageTitle: 'cpmApp.purchaseItem.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/contract/purchase-item/purchase-item-detail.html',
                    controller: 'PurchaseItemDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('purchaseItem');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'PurchaseItem', function($stateParams, PurchaseItem) {
                    return PurchaseItem.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'purchase-item',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('purchase-item-detail.edit', {
            parent: 'purchase-item-detail',
            url: '/edit',
            data: {
                authorities: ['ROLE_CONTRACT_PURCHASE']
            },
            views:{
            	'content@':{
            		templateUrl: 'app/contract/purchase-item/purchase-item-dialog.html',
            		controller: 'PurchaseItemDialogController',
            		controllerAs: 'vm'
            	}
            },
            resolve: {
            	translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('purchaseItem');
                    return $translate.refresh();
                }],
                entity: ['PurchaseItem','$stateParams', function(PurchaseItem,$stateParams) {
                    return PurchaseItem.get({id : $stateParams.id}).$promise;
                }],
                budgetEntity:function(){
                	return null;
                },
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'purchase-item-detail',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('purchase-item.new', {
            parent: 'purchase-item',
            url: '/new',
            data: {
                authorities: ['ROLE_CONTRACT_PURCHASE']
            },
            views:{
            	'content@':{
            		templateUrl: 'app/contract/purchase-item/purchase-item-dialog.html',
            		controller: 'PurchaseItemDialogController',
            		controllerAs: 'vm'
            	}
            },
            resolve: {
           	 translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('purchaseItem');
                    return $translate.refresh();
                }],
                entity: function () {
                    return {
                        contractId: null,
                        budgetId: null,
                        name: null,
                        quantity: null,
                        price: null,
                        units: null,
                        type: null,
                        source: null,
                        purchaser: null,
                        totalAmount: null,
                        status: null,
                        creator: null,
                        createTime: null,
                        updator: null,
                        updateTime: null,
                        id: null,
                        contractNum: null,
                        contractName: null,
                        budgetOriginal:0
                    };
                },
                budgetEntity:function(){
                	return null;
                },
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'purchase-item',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('purchase-item.edit', {
            parent: 'purchase-item',
            url: '/edit/{id}',
            data: {
                authorities: ['ROLE_CONTRACT_PURCHASE'],
                pageTitle: 'cpmApp.purchaseItem.detail.title'
            },
            views: {
            	'content@' : {
            		templateUrl: 'app/contract/purchase-item/purchase-item-dialog.html',
            		controller: 'PurchaseItemDialogController',
            		controllerAs: 'vm'
            	}
            },
            resolve: {
            	translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('purchaseItem');
                    return $translate.refresh();
                }],
                entity: ['PurchaseItem','$stateParams', function(PurchaseItem,$stateParams) {
                	return PurchaseItem.get({id : $stateParams.id}).$promise;
                }],
                budgetEntity:function(){
                	return null;
                },
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'purchase-item',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('purchase-item.delete', {
            parent: 'purchase-item',
            url: '/delete/{id}',
            data: {
                authorities: ['ROLE_CONTRACT_PURCHASE']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/contract/purchase-item/purchase-item-delete-dialog.html',
                    controller: 'PurchaseItemDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['PurchaseItem', function(PurchaseItem) {
                            return PurchaseItem.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('purchase-item', null, { reload: 'purchase-item' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
