(function() {
    'use strict';

    angular
        .module('cpmApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('contract-info', {
            parent: 'contract',
            url: '/contract-info?page&sort&serialNum&name&type&isPrepared&isEpibolic',
            data: {
                authorities: ['ROLE_CONTRACT_INFO'],
                pageTitle: 'cpmApp.contractInfo.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/contract/contract-info/contract-infos.html',
                    controller: 'ContractInfoController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'wci.id,desc',
                    squash: true
                },
                serialNum:null,
                name:null,
                type:null,
                isPrepared:null,
                isEpibolic:null
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
            		return $ocLazyLoad.load([
            		                         'app/info/dept-info/dept-info.service.js',
            		                         'app/contract/contract-info/outsourcing-user.service.js',
            		                         'app/contract/contract-info/contract-info.service.js',
            		                         'app/contract/contract-info/contract-info.controller.js'
            		                        ]);
                }],
                pagingParams: ['$state','$stateParams', 'PaginationUtil', function ($state,$stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        serialNum: $stateParams.serialNum,
                        name: $stateParams.name,
                        type: $stateParams.type,
                        isPrepared: $stateParams.isPrepared,
                        isEpibolic: $stateParams.isEpibolic
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('contractInfo');
                    $translatePartialLoader.addPart('deptInfo');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('contract-info.query', {
            parent: 'contract-info',
            url: '/queryDept?selectType&showChild',
            data: {
                authorities: ['ROLE_CONTRACT_INFO']
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
        .state('contract-info-detail', {
            parent: 'contract-info',
            url: '/detail/{id}',
            data: {
                authorities: ['ROLE_CONTRACT_INFO'],
                pageTitle: 'cpmApp.contractInfo.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/contract/contract-info/contract-info-detail.html',
                    controller: 'ContractInfoDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
            		return $ocLazyLoad.load('app/contract/contract-info/contract-info-detail.controller.js');
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('contractInfo');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', '$ocLazyLoad','$injector', function($stateParams, $ocLazyLoad,$injector) {
                	return $ocLazyLoad.load('app/contract/contract-info/contract-info.service.js').then(
                			function(){
                				return $injector.get('ContractInfo').get({id : $stateParams.id}).$promise;
                			}
                	);
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'contract-info',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('contract-info.new',{
        	parent: 'contract-info',
            url: '/new',
            data: {
                authorities: ['ROLE_CONTRACT_END']
            },
            views:{
            	'content@':{
            		templateUrl: 'app/contract/contract-info/contract-info-dialog.html',
                    controller: 'ContractInfoDialogController',
                    controllerAs: 'vm'
            	}
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
            		return $ocLazyLoad.load('app/contract/contract-info/contract-info-dialog.controller.js');
                }],
            	 translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                     $translatePartialLoader.addPart('contractInfo');
                     $translatePartialLoader.addPart('outsourcingUser');
                     $translatePartialLoader.addPart('deptInfo');
                     $translatePartialLoader.addPart('global');
                     return $translate.refresh();
                 }],
                 entity: ['$stateParams', function($stateParams) {
                     return {
                    	 mark: $stateParams.mark
                     }
                 }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                    	queryDept:'contract-info.new.queryDept',
                    	creatOutsourcingUser:'contract-info.new.creatOutsourcingUser',
                    	changeOutsourcingUser:'contract-info.new.changeOutsourcingUser',
                        name: $state.current.name || 'contract-info',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('contract-info.new.creatOutsourcingUser',{
        	parent:'contract-info.new',
        	url:'/creatOutsourcingUser/{mark}/{contractId}',
        	data:{
        		authorities: ['ROLE_CONTRACT_END']
        	},
        	onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                	templateUrl: 'app/contract/contract-info/outsourcing-user-dialog.html',
                	controller: 'OutsourcingUserController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    params: {
                    	mark: null,
                    	contractId: null
            		},
                    resolve: {
                    	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    		return $ocLazyLoad.load('app/contract/contract-info/outsourcing-user-dialog.controller.js');
                        }],
                        entity: function() {
                            return {
                            	mark : $stateParams.mark,
                            	contractId : $stateParams.contractId
                            }
                        }
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false});
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('contract-info.new.changeOutsourcingUser',{
        	parent:'contract-info.new',
        	url:'/changeOutsourcingUser/{infoId}',
        	data:{
        		authorities: ['ROLE_CONTRACT_END']
        	},
        	onEnter: ['$stateParams', '$state', '$uibModal','OutsourcingUser',function($stateParams, $state, $uibModal,OutsourcingUser) {
                $uibModal.open({
        			templateUrl: 'app/contract/contract-info/outsourcing-user-dialog.html',
                	controller: 'OutsourcingUserController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                    	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    		return $ocLazyLoad.load('app/contract/contract-info/outsourcing-user-dialog.controller.js');
                        }],
                        entity: function() {
                        	return OutsourcingUser.get({infoId: $stateParams.infoId}).$promise;
                        }
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false});
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('contract-info.new.queryDept', {
            parent: 'contract-info.new',
            url: '/queryDept?selectType&showChild&dataType',
            data: {
                authorities: ['ROLE_CONTRACT_END']
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
        .state('contract-info.edit',{
        	parent:'contract-info',
        	url:'/edit/{id}',
        	data:{
        		authorities: ['ROLE_CONTRACT_END']
        	},
        	views:{
        		'content@':{
        			templateUrl: 'app/contract/contract-info/contract-info-dialog.html',
        			controller: 'ContractInfoDialogController',
        			controllerAs: 'vm',
        		}
        	},
        	resolve:{
        		loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
            		return $ocLazyLoad.load('app/contract/contract-info/contract-info-dialog.controller.js');
                }],
        		 translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                     $translatePartialLoader.addPart('contractInfo');
                     $translatePartialLoader.addPart('outsourcingUser');
                     $translatePartialLoader.addPart('deptInfo');
                     $translatePartialLoader.addPart('global');
                     return $translate.refresh();
                 }],
                 entity: ['$stateParams', '$ocLazyLoad','$injector', function($stateParams, $ocLazyLoad,$injector) {
                 	return $ocLazyLoad.load('app/contract/contract-info/contract-info.service.js').then(
                 			function(){
                 				return $injector.get('ContractInfo').get({id : $stateParams.id}).$promise;
                 			}
                 	);
                 }],
                 previousState: ["$state", function ($state) {
                     var currentStateData = {
                    	 queryDept:'contract-info.edit.queryDept',
                    	 changeOutsourcingUser: 'contract-info.edit.changeOutsourcingUser',
                    	 creatOutsourcingUser: 'contract-info.edit.creatOutsourcingUser',
                         name: $state.current.name || 'contract-info',
                         params: $state.params,
                         url: $state.href($state.current.name, $state.params)
                     };
                     return currentStateData;
                 }]
                 
        	}
        })
        .state('contract-info.edit.creatOutsourcingUser',{
        	parent:'contract-info.edit',
        	url:'/creatOutsourcingUser',
        	data:{
        		authorities: ['ROLE_CONTRACT_END']
        	},
        	onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
        			templateUrl: 'app/contract/contract-info/outsourcing-user-dialog.html',
                	controller: 'OutsourcingUserController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    params: {
                    	contractId: null
            		},
                    resolve: {
                    	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    		return $ocLazyLoad.load('app/contract/contract-info/outsourcing-user-dialog.controller.js');
                        }],
                        entity: function() {
                            return {
                            	contractId : $stateParams.id
                            }
                        }
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false});
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('contract-info.edit.queryDept', {
            parent: 'contract-info.edit',
            url: '/queryDept?selectType&showChild&dataType',
            data: {
                authorities: ['ROLE_CONTRACT_END']
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
        .state('contract-info.finRate',{
        	parent:'contract-info',
        	url:'/finRate/{id}',
        	data:{
        		authorities: ['ROLE_CONTRACT_INFO']
        	},
        	views:{
        		'content@':{
        			templateUrl: 'app/contract/contract-info/contract-info-finRate.html',
        			controller: 'ContractInfofinRateController',
        			controllerAs: 'vm',
        		}
        	},
        	resolve:{
        		loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
            		return $ocLazyLoad.load('app/contract/contract-info/contract-info-finRate.controller.js');
                }],
        		 translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                     $translatePartialLoader.addPart('contractInfo');
                     $translatePartialLoader.addPart('global');
                     return $translate.refresh();
                 }],
                 entity: ['$stateParams', '$ocLazyLoad','$injector', function($stateParams, $ocLazyLoad,$injector) {
                 	return $ocLazyLoad.load('app/contract/contract-info/contract-info.service.js').then(
                 			function(){
                 				return $injector.get('ContractInfo').get({id : $stateParams.id}).$promise;
                 			}
                 	);
                 }],
        	}
        })
        .state('contract-info.end', {
            parent: 'contract-info',
            url: '/end/{id}',
            data: {
                authorities: ['ROLE_CONTRACT_END']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/contract/contract-info/contract-info-end-dialog.html',
                    controller: 'ContractInfoEndController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                    	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    		return $ocLazyLoad.load('app/contract/contract-info/contract-info-end-dialog.controller.js');
                        }],
                        entity: ['ContractInfo', function(ContractInfo) {
                            return ContractInfo.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('contract-info', null, { reload: 'contract-info' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('contract-info.delete', {
            parent: 'contract-info',
            url: '/delete/{id}',
            data: {
                authorities: ['ROLE_CONTRACT_END']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/contract/contract-info/contract-info-delete-dialog.html',
                    controller: 'ContractInfoDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                    	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    		return $ocLazyLoad.load('app/contract/contract-info/contract-info-delete-dialog.controller.js');
                        }],
                        entity: ['ContractInfo', function(ContractInfo) {
                            return ContractInfo.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('contract-info', null, { reload: 'contract-info' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('contract-info.edit.changeOutsourcingUser',{
        	parent:'contract-info.edit',
        	url:'/changeOutsourcingUser/{infoId}',
        	data:{
        		authorities: ['ROLE_CONTRACT_END']
        	},
        	onEnter: ['$stateParams', '$state', '$uibModal','OutsourcingUser',function($stateParams, $state, $uibModal,OutsourcingUser) {
                $uibModal.open({
        			templateUrl: 'app/contract/contract-info/outsourcing-user-dialog.html',
                	controller: 'OutsourcingUserController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                    	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    		return $ocLazyLoad.load('app/contract/contract-info/outsourcing-user-dialog.controller.js');
                        }],
                        entity: function() {
                        	return OutsourcingUser.get({infoId : $stateParams.infoId}).$promise;
                        }
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false});
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('contract-info.import', {
            parent: 'contract-info',
            url: '/upload',
            data: {
                authorities: ['ROLE_CONTRACT_END'],
            },
            views: {
                'content@': {
                    templateUrl: 'app/contract/contract-info/contract-info-import.html',
                    controller: 'ContractInfoImportController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
            		return $ocLazyLoad.load('app/contract/contract-info/contract-info-import.controller.js');
                }],
            	translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('contractInfo');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
                previousState: ["$state", function ($state) {
                	var currentStateData = {
            			name: $state.current.name || 'contract-info',
            			params: $state.params,
            			url: $state.href($state.current.name, $state.params)
                	};
                	return currentStateData;
	            }]
            }
        })
        ;
    }

})();
