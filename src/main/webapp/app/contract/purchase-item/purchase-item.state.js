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
            url: '/purchase-item?name&contractId&source&ppType',
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
                },
                name: null,
                contractId: null,
                source: null,
                ppType: null
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    return $ocLazyLoad.load([
                                             "app/contract/contract-budget/contract-budget.service.js",  
                                             'app/info/dept-info/dept-info.service.js',
                                             'app/contract/purchase-item/purchase-item.service.js',
                                             'app/contract/purchase-item/purchase-item.controller.js']);
                }],
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        name: $stateParams.name,
                        contractId: $stateParams.contractId,
                        source: $stateParams.source,
                        ppType: $stateParams.ppType
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
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
            		return $ocLazyLoad.load('app/contract/purchase-item/purchase-item-detail.controller.js');
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('purchaseItem');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', '$ocLazyLoad','$injector', function($stateParams, $ocLazyLoad,$injector) {
                	return $ocLazyLoad.load('app/contract/purchase-item/purchase-item.service.js').then(
                			function(){
                				return $injector.get('PurchaseItem').get({id : $stateParams.id}).$promise;
                			}
                	);
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
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
            		return $ocLazyLoad.load('app/contract/purchase-item/purchase-item-dialog.controller.js');
                }],
            	translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
            		$translatePartialLoader.addPart('purchaseItem');
            		$translatePartialLoader.addPart('productPrice');
            		$translatePartialLoader.addPart('deptInfo');
            		$translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', '$ocLazyLoad','$injector', function($stateParams, $ocLazyLoad,$injector) {
                	return $ocLazyLoad.load('app/contract/purchase-item/purchase-item.service.js').then(
                			function(){
                				return $injector.get('PurchaseItem').get({id : $stateParams.id}).$promise;
                			}
                	);
                }],
                budgetEntity:function(){
                	return null;
                },
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                    	choseProject: 'purchase-item.choseProject',
                    	queryDept:'purchase-item.edit.queryDept',
                        name: $state.current.name || 'purchase-item',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('purchase-item.edit.queryDept', {
            parent: 'purchase-item.edit',
            url: '/queryDept?selectType&showChild&showUser',
            data: {
                authorities: ['ROLE_CONTRACT_PURCHASE']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/info/dept-info/dept-info-query.html',
                    controller: 'DeptInfoQueryController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                    	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    		return $ocLazyLoad.load('app/info/dept-info/dept-info-query.controller.js');
                        }],
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
        .state('purchase-item.choseProject', {
            parent: 'purchase-item.edit',
            url: '/queryProductPrice?type',
            data: {
            	authorities: ['ROLE_CONTRACT_PURCHASE'],
                pageTitle: 'cpmApp.projectInfo.home.title'
            },
            onEnter: ['$stateParams','$state','$uibModal',function($stateParams,$state,$uibModal){
            	$uibModal.open({
            		templateUrl: 'app/contract/purchase-item/purchase-item-choseProject.html',
            		controller: 'ChoseProjectsController',
            		controllerAs: 'vm',
            		backdrop: 'static',
            		size: 'lg',
            		params: {
            			type: null
            		},
            		resolve: {
            			loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    		return $ocLazyLoad.load('app/contract/purchase-item/purchase-item-choseProject.controller.js');
                        }],
            			entity:function () {
            				return {
            					type: $stateParams.type
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
                    	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    		return $ocLazyLoad.load('app/contract/purchase-item/purchase-item-delete-dialog.controller.js');
                        }],
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
        })
    }
})();
