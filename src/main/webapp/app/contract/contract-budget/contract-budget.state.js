(function() {
    'use strict';

    angular
        .module('cpmApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('contract-budget', {
            parent: 'contract',
            url: '/contract-budget?page&sort&contractId&name&purchaseType',
            data: {
                authorities: ['ROLE_CONTRACT_BUDGET'],
                pageTitle: 'cpmApp.contractBudget.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/contract/contract-budget/contract-budgets.html',
                    controller: 'ContractBudgetController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'wcb.id,desc',
                    squash: true
                },
                serialNum: null,
                name: null,
                contractName: null
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    return $ocLazyLoad.load([
                                             'app/project/project-info/project-info.service.js',
                                             'app/contract/purchase-item/purchase-item.service.js',
                                             'app/info/dept-info/dept-info.service.js',
                                             'app/contract/contract-budget/contract-budget.service.js',
                                             'app/contract/contract-budget/contract-budget.controller.js']);
                }],
                pagingParams: ["$state",'$stateParams', 'PaginationUtil', function ($state,$stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        contractId: $stateParams.contractId,
                        name: $stateParams.name,
                        purchaseType: $stateParams.purchaseType
                        
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('contractBudget');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('contract-budget-detail', {
            parent: 'contract-budget',
            url: '/detail/{id}',
            data: {
                authorities: ['ROLE_CONTRACT_BUDGET'],
                pageTitle: 'cpmApp.contractBudget.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/contract/contract-budget/contract-budget-detail.html',
                    controller: 'ContractBudgetDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    return $ocLazyLoad.load('app/contract/contract-budget/contract-budget-detail.controller.js');
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('contractBudget');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', '$ocLazyLoad','$injector', function($stateParams, $ocLazyLoad,$injector) {
                	return $ocLazyLoad.load('app/contract/contract-budget/contract-budget.service.js').then(
                			function(){
                				return $injector.get('ContractBudget').get({id : $stateParams.id}).$promise;
                			}
                	);
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'contract-budget',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('contract-budget.new', {
            parent: 'contract-budget',
            url: '/new',
            data: {
                authorities: ['ROLE_CONTRACT_BUDGET']
            },
            views: {
            	'content@': {
            		templateUrl: 'app/contract/contract-budget/contract-budget-dialog.html',
            		controller: 'ContractBudgetDialogController',
            		controllerAs: 'vm'
            	}
            },
           	resolve: {
           		loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    return $ocLazyLoad.load('app/contract/contract-budget/contract-budget-dialog.controller.js');
                }],
           		translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('contractBudget');
                    $translatePartialLoader.addPart('deptInfo');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
                entity: function () {
                    return {
                    	serialNum: null,
                    	name: null,
                    	contractName: null,
                        userName: null,
                        dept: null,
                        purchaseType: 3,
                        budgetTotal: null,
                        id: null
                    };
                },
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                    	queryDept:'contract-budget.new.queryDept',
                        name: $state.current.name || 'contract-budget',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('contract-budget.new.queryDept', {
            parent: 'contract-budget.new',
            url: '/queryDept?selectType&showChild',
            data: {
                authorities: ['ROLE_CONTRACT_BUDGET']
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
        })
        .state('contract-budget.edit', {
            parent: 'contract-budget',
            url: '/edit/{id}',
            data: {
                authorities: ['ROLE_CONTRACT_BUDGET']
            },
            views: {
            	'content@': {
                    templateUrl: 'app/contract/contract-budget/contract-budget-dialog.html',
                    controller: 'ContractBudgetDialogController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    return $ocLazyLoad.load('app/contract/contract-budget/contract-budget-dialog.controller.js');
                }],
            	translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
	                $translatePartialLoader.addPart('contractBudget');
	                $translatePartialLoader.addPart('deptInfo');
	                $translatePartialLoader.addPart('global');
	                return $translate.refresh();
                }],
                entity: ['$stateParams', '$ocLazyLoad','$injector', function($stateParams, $ocLazyLoad,$injector) {
                	return $ocLazyLoad.load('app/contract/contract-budget/contract-budget.service.js').then(
                			function(){
                				return $injector.get('ContractBudget').get({id : $stateParams.id}).$promise;
                			}
                	);
                }],
                 previousState: ["$state", function ($state) {
 	                var currentStateData = {
 	                	queryDept:'contract-budget.edit.queryDept',
 	                    name: $state.current.name || 'contract-budget',
 	                    params: $state.params,
 	                    url: $state.href($state.current.name, $state.params)
 	                };
 	                return currentStateData;
 	            }]
             }
        })
        .state('contract-budget.edit.queryDept', {
            parent: 'contract-budget.edit',
            url: '/queryDept?selectType&showChild',
            data: {
                authorities: ['ROLE_CONTRACT_BUDGET']
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
        })
        .state('contract-budget.delete', {
            parent: 'contract-budget',
            url: '/delete/{id}',
            data: {
                authorities: ['ROLE_CONTRACT_BUDGET']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/contract/contract-budget/contract-budget-delete-dialog.html',
                    controller: 'ContractBudgetDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                    	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                            return $ocLazyLoad.load('app/contract/contract-budget/contract-budget-delete-dialog.controller.js');
                        }],
                        entity: ['ContractBudget', function(ContractBudget) {
                            return ContractBudget.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('contract-budget', null, { reload: 'contract-budget' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('contract-budget.createProject', {
            parent: 'contract-budget',
            url: '/createProject/{id}',
            data: {
                authorities: ['ROLE_PROJECT_INFO_END'],
                pageTitle: 'cpmApp.projectInfo.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/project/project-info/project-info-dialog.html',
                    controller: 'ProjectInfoDialogController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    return $ocLazyLoad.load('app/project/project-info/project-info-dialog.controller.js');
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('projectInfo');
                    $translatePartialLoader.addPart('deptInfo');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
                entity: function () {
                    return {
                        serialNum: null,
                        contractId: null,
                        budgetId: null,
                        name: null,
                        pm: null,
                        dept: null,
                        startDay: null,
                        endDay: null,
                        budgetTotal: null,
                        status: null,
                        creator: null,
                        createTime: null,
                        updator: null,
                        updateTime: null,
                        id: null,
                        budgetOriginal:0
                    };
                },
                budgetEntity: ['$stateParams','ContractBudget', function($stateParams,ContractBudget) {
                    return ContractBudget.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                    	queryDept:'contract-budget.createProject.queryDept',
                        name: $state.current.name || 'contract-budget',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('contract-budget.createProject.queryDept', {
            parent: 'contract-budget.createProject',
            url: '/queryDept?selectType&showChild',
            data: {
                authorities: ['ROLE_PROJECT_INFO_END']
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
        })
        .state('contract-budget.createPurchaseItem', {
            parent: 'contract-budget',
            url: '/createPurchaseItem/{id}',
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
                budgetEntity: ['$stateParams','ContractBudget', function($stateParams,ContractBudget) {
                    return ContractBudget.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                    	choseProject:'contract-budget.createPurchaseItem.choseProject',
                    	queryDept:'contract-budget.createPurchaseItem.queryDept',
                        name: $state.current.name || 'contract-budget',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('contract-budget.createPurchaseItem.queryDept', {
            parent: 'contract-budget.createPurchaseItem',
            url: '/queryDept?selectType&showChild&showUser',
            data: {
                authorities: ['ROLE_INFO_BASIC']
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
        .state('contract-budget.createPurchaseItem.choseProject', {
            parent: 'contract-budget.createPurchaseItem',
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
            			type:null
            		},
            		resolve: {
            			loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                            return $ocLazyLoad.load('app/contract/purchase-item/purchase-item-choseProject.controller.js');
                        }],
            			entity:function () {
            				return {
            					type : $stateParams.type
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
