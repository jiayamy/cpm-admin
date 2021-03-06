(function() {
    'use strict';

    angular
        .module('cpmApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('contract-receive', {
            parent: 'contract',
            url: '/contract-receive?page&sort&contractId',
            data: {
                authorities: ['ROLE_CONTRACT_RECEIVE'],
                pageTitle: 'cpmApp.contractReceive.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/contract/contract-receive/contract-receives.html',
                    controller: 'ContractReceiveController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'wcr.id,desc',
                    squash: true
                },
                contractId: null
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    return $ocLazyLoad.load([
                                             'app/info/dept-info/dept-info.service.js',
                                             'app/contract/contract-info/contract-info.service.js',
                                             'app/contract/contract-receive/contract-receive.service.js',
                                             'app/contract/contract-receive/contract-receive.controller.js']);
                }],
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        contractId: $stateParams.contractId
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('contractReceive');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('contract-receive-detail', {
            parent: 'contract-receive',
            url: '/detail/{id}',
            data: {
                authorities: ['ROLE_CONTRACT_RECEIVE'],
                pageTitle: 'cpmApp.contractReceive.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/contract/contract-receive/contract-receive-detail.html',
                    controller: 'ContractReceiveDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    return $ocLazyLoad.load('app/contract/contract-receive/contract-receive-detail.controller.js');
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('contractReceive');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', '$ocLazyLoad','$injector', function($stateParams, $ocLazyLoad,$injector) {
                	return $ocLazyLoad.load('app/contract/contract-receive/contract-receive.service.js').then(
                			function(){
                				return $injector.get('ContractReceive').get({id : $stateParams.id}).$promise;
                			}
                	);
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'contract-receive',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('contract-receive.new',{
        	parent:'contract-receive',
        	url:'/new',
        	data:{
        		 authorities: ['ROLE_CONTRACT_RECEIVE']
        	},
        	views:{
        		'content@':{
        		   templateUrl: 'app/contract/contract-receive/contract-receive-dialog.html',
                   controller: 'ContractReceiveDialogController',
                   controllerAs: 'vm'
        		}
        	},
        	resolve:{
        		loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    return $ocLazyLoad.load('app/contract/contract-receive/contract-receive-dialog.controller.js');
                }],
	    		 translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
	                 $translatePartialLoader.addPart('contractReceive');
	                 $translatePartialLoader.addPart('deptInfo');
	                 $translatePartialLoader.addPart('global');
	                 return $translate.refresh();
	             }],
	             entity: function () {
	             return {
	                   contractId: null,
	                   receiveTotal: null,
	                   receiveDay: null,
	                   status: null,
	                   creator: null,
	                   createTime: null,
	                   updator: null,
	                   updateTime: null,
	                   receiver: null,
	                   id: null
	               };
	           },
	           previousState: ["$state", function ($state) {
                   var currentStateData = {
                   	queryDept:'contract-receive.new.queryDept',
                       name: $state.current.name || 'contract-receive',
                       params: $state.params,
                       url: $state.href($state.current.name, $state.params)
                   };
                   return currentStateData;
               }]
        	
        	}
        })
        .state('contract-receive.new.queryDept', {
            parent: 'contract-receive.new',
            url: '/queryDept?selectType&showChild&dataType',
            data: {
                authorities: ['ROLE_CONTRACT_RECEIVE']
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
                            	dataType:$stateParams.dataType
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

        .state('contract-receive.edit',{
        	parent: 'contract-receive',
            url: '/edit/{id}',
            data: {
                authorities: ['ROLE_CONTRACT_RECEIVE']
            },
            views:{
            	'content@':{
            	   templateUrl: 'app/contract/contract-receive/contract-receive-dialog.html',
     	           controller: 'ContractReceiveDialogController',
     	           controllerAs: 'vm'
            	}
            },
            resolve:{
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    return $ocLazyLoad.load('app/contract/contract-receive/contract-receive-dialog.controller.js');
                }],
            	translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('contractReceive');
                    $translatePartialLoader.addPart('deptInfo');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', '$ocLazyLoad','$injector', function($stateParams, $ocLazyLoad,$injector) {
                	return $ocLazyLoad.load('app/contract/contract-receive/contract-receive.service.js').then(
                			function(){
                				return $injector.get('ContractReceive').get({id : $stateParams.id}).$promise;
                			}
                	);
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        queryDept:'contract-receive.edit.queryDept',
                        name: $state.current.name || 'contract-receive',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('contract-receive.edit.queryDept', {
            parent: 'contract-receive.edit',
            url: '/queryDept?selectType&showChild&dataType',
            data: {
                authorities: ['ROLE_CONTRACT_RECEIVE']
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
                            	dataType:$stateParams.dataType
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
        .state('contract-receive.delete', {
            parent: 'contract-receive',
            url: '/delete/{id}',
            data: {
                authorities: ['ROLE_CONTRACT_RECEIVE']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/contract/contract-receive/contract-receive-delete-dialog.html',
                    controller: 'ContractReceiveDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                    	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                            return $ocLazyLoad.load('app/contract/contract-receive/contract-receive-delete-dialog.controller.js');
                        }],
                        entity: ['ContractReceive', function(ContractReceive) {
                            return ContractReceive.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('contract-receive', null, { reload: 'contract-receive' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
