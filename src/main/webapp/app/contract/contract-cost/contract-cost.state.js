(function() {
    'use strict';

    angular
        .module('cpmApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('contract-cost', {
            parent: 'contract',
            url: '/contract-cost?page&sort&contractId&type&name',
            data: {
                authorities: ['ROLE_CONTRACT_COST'],
                pageTitle: 'cpmApp.contractCost.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/contract/contract-cost/contract-costs.html',
                    controller: 'ContractCostController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'wcc.costDay,desc',
                    squash: true
                },
                contractId: null,
                type: null,
                name: null
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    return $ocLazyLoad.load([
                                             'app/info/dept-info/dept-info.service.js',
                                             'app/contract/contract-budget/contract-budget.service.js',
                                             'app/contract/contract-info/contract-info.service.js',
                                             'app/contract/contract-cost/contract-cost.service.js',
                                             'app/contract/contract-cost/contract-cost.controller.js']);
                }],
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        contractId: $stateParams.contractId,
                        type: $stateParams.type,
                        name: $stateParams.name
                    };
                }],
                pageType:function(){
                	return 2;
                },
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('contractCost');
                    $translatePartialLoader.addPart('contractInfo');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('contract-cost-timesheet', {
            parent: 'contract',
            url: '/contract-cost-timesheet?page&sort&contractId&type&name',
            data: {
                authorities: ['ROLE_CONTRACT_COST'],
                pageTitle: 'cpmApp.contractCost.home.timesheetTitle'
            },
            views: {
                'content@': {
                    templateUrl: 'app/contract/contract-cost/contract-costs.html',
                    controller: 'ContractCostController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'wcc.costDay,desc',
                    squash: true
                },
                contractId: null,
                type: null,
                name: null
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    return $ocLazyLoad.load([
                                             'app/contract/contract-info/contract-info.service.js',
                                             'app/contract/contract-cost/contract-cost.service.js',
                                             'app/contract/contract-cost/contract-cost.controller.js']);
                }],
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        contractId: $stateParams.contractId,
                        type: $stateParams.type,
                        name: $stateParams.name
                    };
                }],
                pageType:function(){
                	return 1;
                },
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('contractCost');
                    $translatePartialLoader.addPart('contractInfo');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('contract-cost-detail', {
            parent: 'contract-cost',
            url: '/detail/{id}',
            data: {
                authorities: ['ROLE_CONTRACT_COST'],
                pageTitle: 'cpmApp.contractCost.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/contract/contract-cost/contract-cost-detail.html',
                    controller: 'ContractCostDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
            		return $ocLazyLoad.load('app/contract/contract-cost/contract-cost-detail.controller.js');
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('contractCost');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', '$ocLazyLoad','$injector', function($stateParams, $ocLazyLoad,$injector) {
                	return $ocLazyLoad.load('app/contract/contract-cost/contract-cost.service.js').then(
                			function(){
                				return $injector.get('ContractCost').get({id : $stateParams.id}).$promise;
                			}
                	);
                }],
                pageType:function(){
                	return 2;
                },
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'contract-cost',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('contract-cost-timesheet-detail', {
            parent: 'contract-cost-timesheet',
            url: '/detail/{id}',
            data: {
                authorities: ['ROLE_CONTRACT_COST'],
                pageTitle: 'cpmApp.contractCost.detail.timesheetTitle'
            },
            views: {
                'content@': {
                    templateUrl: 'app/contract/contract-cost/contract-cost-detail.html',
                    controller: 'ContractCostDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
            		return $ocLazyLoad.load('app/contract/contract-cost/contract-cost-detail.controller.js');
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('contractCost');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', '$ocLazyLoad','$injector', function($stateParams, $ocLazyLoad,$injector) {
                	return $ocLazyLoad.load('app/contract/contract-cost/contract-cost.service.js').then(
                			function(){
                				return $injector.get('ContractCost').get({id : $stateParams.id}).$promise;
                			}
                	);
                }],
                pageType:function(){
                	return 1;
                },
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'contract-cost',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('contract-cost.new',{
        	parent: 'contract-cost',
            url: '/new',
            data: {
                authorities: ['ROLE_CONTRACT_COST']
            },
            views:{
            	'content@':{
	        		 templateUrl: 'app/contract/contract-cost/contract-cost-dialog.html',
	                 controller: 'ContractCostDialogController',
	                 controllerAs: 'vm'
            	}
            },
        	resolve:{
        		loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
            		return $ocLazyLoad.load('app/contract/contract-cost/contract-cost-dialog.controller.js');
                }],
        		 translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                     $translatePartialLoader.addPart('contractCost');
                     $translatePartialLoader.addPart('deptInfo');
                     $translatePartialLoader.addPart('global');
                     return $translate.refresh();
                 }],
                 entity: function () {
                     return {
                         contractId: null,
                         budgetId: null,
                         deptId: null,
                         dept: null,
                         name: null,
                         type: null,
                         total: null,
                         costDesc: null,
                         status: null,
                         creator: null,
                         createTime: null,
                         updator: null,
                         updateTime: null,
                         id: null
                     };
                 },
                 previousState: ["$state", function ($state) {
                     var currentStateData = {
                         queryDept:'contract-cost.new.queryDept',
                         name: $state.current.name || 'contract-cost',
                         params: $state.params,
                         url: $state.href($state.current.name, $state.params)
                     };
                     return currentStateData;
                 }]
                 
        	}
        })
        .state('contract-cost.new.queryDept', {
            parent: 'contract-cost.new',
            url: '/queryDept?selectType&showChild&showUser',
            data: {
                authorities: ['ROLE_CONTRACT_COST']
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
        .state('contract-cost.edit',{
        	parent: 'contract-cost',
            url: '/edit/{id}',
            data: {
                authorities: ['ROLE_CONTRACT_COST']
            },
            views:{
            	'content@':{
            		templateUrl: 'app/contract/contract-cost/contract-cost-dialog.html',
                    controller: 'ContractCostDialogController',
                    controllerAs: 'vm'
            	}
            },
            resolve:{
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
            		return $ocLazyLoad.load('app/contract/contract-cost/contract-cost-dialog.controller.js');
                }],
            	translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('contractCost');
                    $translatePartialLoader.addPart('deptInfo');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', '$ocLazyLoad','$injector', function($stateParams, $ocLazyLoad,$injector) {
                	return $ocLazyLoad.load('app/contract/contract-cost/contract-cost.service.js').then(
                			function(){
                				return $injector.get('ContractCost').get({id : $stateParams.id}).$promise;
                			}
                	);
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                    	queryDept:'contract-cost.edit.queryDept',
                        name: $state.current.name || 'contract-cost',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('contract-cost.edit.queryDept', {
            parent: 'contract-cost.edit',
            url: '/queryDept?selectType&showChild&showUser',
            data: {
                authorities: ['ROLE_CONTRACT_COST']
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
        .state('contract-cost.delete', {
            parent: 'contract-cost',
            url: '/delete/{id}',
            data: {
                authorities: ['ROLE_CONTRACT_COST']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/contract/contract-cost/contract-cost-delete-dialog.html',
                    controller: 'ContractCostDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                    	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    		return $ocLazyLoad.load('app/contract/contract-cost/contract-cost-delete-dialog.controller.js');
                        }],
                        entity: ['ContractCost', function(ContractCost) {
                            return ContractCost.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('contract-cost', null, { reload: 'contract-cost' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('contract-cost.import', {
            parent: 'contract-cost',
            url: '/upload',
            data: {
                authorities: ['ROLE_CONTRACT_COST'],
            },
            views: {
                'content@': {
                    templateUrl: 'app/contract/contract-cost/contract-cost-import.html',
                    controller: 'ContractCostImportController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
            		return $ocLazyLoad.load('app/contract/contract-cost/contract-cost-import.controller.js');
                }],
            	translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('contractCost');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
                previousState: ["$state", function ($state) {
                	var currentStateData = {
            			name: $state.current.name || 'contract-cost',
            			params: $state.params,
            			url: $state.href($state.current.name, $state.params)
                	};
                	return currentStateData;
	            }]
            }
        });
    }

})();
