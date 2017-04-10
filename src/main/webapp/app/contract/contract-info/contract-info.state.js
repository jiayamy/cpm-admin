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
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('contractInfo');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'ContractInfo', function($stateParams, ContractInfo) {
                    return ContractInfo.get({id : $stateParams.id}).$promise;
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
       .state('contract-info-detail.edit',{
        	parent:'contract-info-detail',
        	url:'/edit',
        	data:{
        		authorities: ['ROLE_CONTRACT_INFO']
        	},
        	views:{
        		'content@':{
        			templateUrl: 'app/contract/contract-info/contract-info-dialog.html',
        			controller: 'ContractInfoDialogController',
        			controllerAs: 'vm'
        		}
        	},
        	resolve:{
        		 translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                     $translatePartialLoader.addPart('contractInfo');
                     $translatePartialLoader.addPart('deptInfo');
                     $translatePartialLoader.addPart('global');
                     return $translate.refresh();
                 }],
                 entity: ['$stateParams', 'ContractInfo', function($stateParams, ContractInfo) {
                     return ContractInfo.get({id : $stateParams.id}).$promise;
                 }],
                 previousState: ["$state", function ($state) {
                     var currentStateData = {
                         queryDept:'contract-info-detail.edit.queryDept',
                         name: $state.current.name || 'contract-info-detail',
                         params: $state.params,
                         url: $state.href($state.current.name, $state.params)
                     };
                     return currentStateData;
                 }]
        	}
        })
        .state('contract-info-detail.edit.queryDept', {
            parent: 'contract-info-detail.edit',
            url: '/queryDept?selectType&showChild&dataType',
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
        .state('contract-info.new',{
        	parent: 'contract-info',
            url: '/new',
            data: {
                authorities: ['ROLE_CONTRACT_INFO']
            },
            views:{
            	'content@':{
            		templateUrl: 'app/contract/contract-info/contract-info-dialog.html',
                    controller: 'ContractInfoDialogController',
                    controllerAs: 'vm'
            	}
            },
            resolve: {
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
        	url:'/creatOutsourcingUser/{mark}',
        	data:{
        		authorities: ['ROLE_CONTRACT_INFO']
        	},
//        	views:{
//        		'content@':{
//        			templateUrl: 'app/contract/contract-info/outsourcing-user-dialog.html',
//        			controller: 'OutsourcingUserController',
//        			controllerAs: 'vm',
//        		}
//        	},
//        	resolve:{
//        		 translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
//                     $translatePartialLoader.addPart('contractInfo');
//                     $translatePartialLoader.addPart('outsourcingUser');
//                     $translatePartialLoader.addPart('global');
//                     return $translate.refresh();
//                 }],
//                 entity: ['$stateParams',function($stateParams) {
//                     return {
//                    	 mark: $stateParams.mark
//                     }
//                 }],
//                 previousState: ["$state", function ($state) {
//                     var currentStateData = {
//                         name: $state.current.name || 'contract-info.new',
//                         params: $state.params,
//                         url: $state.href($state.current.name, $state.params)
//                     };
//                     return currentStateData;
//                 }]
//                 
//        	}
        	onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                	templateUrl: 'app/contract/contract-info/outsourcing-user-dialog.html',
                	controller: 'OutsourcingUserController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    params: {
                    	mark: null
            		},
                    resolve: {
                        entity: function() {
                            return {
                            	mark : $stateParams.mark
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
        	url:'/changeOutsourcingUser/{id}',
        	data:{
        		authorities: ['ROLE_CONTRACT_INFO']
        	},
        	onEnter: ['$stateParams', '$state', '$uibModal','OutsourcingUser',function($stateParams, $state, $uibModal,OutsourcingUser) {
                $uibModal.open({
        			templateUrl: 'app/contract/contract-info/outsourcing-user-dialog.html',
                	controller: 'OutsourcingUserController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function() {
                        	return OutsourcingUser.choseUser({id : $stateParams.id}).$promise;
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
        		authorities: ['ROLE_CONTRACT_INFO']
        	},
        	views:{
        		'content@':{
        			templateUrl: 'app/contract/contract-info/contract-info-dialog.html',
        			controller: 'ContractInfoDialogController',
        			controllerAs: 'vm',
        		}
        	},
        	resolve:{
        		 translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                     $translatePartialLoader.addPart('contractInfo');
                     $translatePartialLoader.addPart('outsourcingUser');
                     $translatePartialLoader.addPart('deptInfo');
                     $translatePartialLoader.addPart('global');
                     return $translate.refresh();
                 }],
                 entity: ['$stateParams', 'ContractInfo', function($stateParams, ContractInfo) {
                     return ContractInfo.get({id : $stateParams.id}).$promise;
                 }],
                 previousState: ["$state", function ($state) {
                     var currentStateData = {
                    	 queryDept:'contract-info.edit.queryDept',
                    	 changeOutsourcingUser: 'contract-info.edit.changeOutsourcingUser',
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
        		authorities: ['ROLE_CONTRACT_INFO']
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
        		 translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                     $translatePartialLoader.addPart('contractInfo');
                     $translatePartialLoader.addPart('global');
                     return $translate.refresh();
                 }],
                 entity: ['$stateParams', 'ContractInfo', function($stateParams, ContractInfo) {
                     return ContractInfo.get({id : $stateParams.id}).$promise;
                 }]
        	}
        })
        .state('contract-info.end', {
            parent: 'contract-info',
            url: '/end/{id}',
            data: {
                authorities: ['ROLE_CONTRACT_INFO']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/contract/contract-info/contract-info-end-dialog.html',
                    controller: 'ContractInfoEndController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
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
                authorities: ['ROLE_CONTRACT_INFO']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/contract/contract-info/contract-info-delete-dialog.html',
                    controller: 'ContractInfoDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
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
        		authorities: ['ROLE_CONTRACT_INFO']
        	},
        	onEnter: ['$stateParams', '$state', '$uibModal','OutsourcingUser',function($stateParams, $state, $uibModal,OutsourcingUser) {
                $uibModal.open({
        			templateUrl: 'app/contract/contract-info/outsourcing-user-dialog.html',
                	controller: 'OutsourcingUserController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
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
                authorities: ['ROLE_CONTRACT_INFO'],
            },
            views: {
                'content@': {
                    templateUrl: 'app/contract/contract-info/contract-info-import.html',
                    controller: 'ContractInfoImportController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
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
