(function() {
    'use strict';

    angular
        .module('cpmApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('user-cost', {
            parent: 'info',
            url: '/user-cost?page&sort&serialNum&userName&costMonth&status',
            data: {
                authorities: ['ROLE_INFO_USERCOST'],
                pageTitle: 'cpmApp.userCost.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/info/user-cost/user-costs.html',
                    controller: 'UserCostController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'wuc.id,desc',
                    squash: true
                },
                serialNum:null,
                userName:null,
                costMonth:null,
                status:null
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    return $ocLazyLoad.load(['app/info/user-cost/user-cost.controller.js',
                                             'app/info/user-cost/user-cost.service.js']);
                }],
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        serialNum:$stateParams.serialNum,
                        userName:$stateParams.userName,
                        costMonth:$stateParams.costMonth,
                        status:$stateParams.status
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('userCost');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('user-cost-detail', {
            parent: 'user-cost',
            url: '/detail/{id}',
            data: {
                authorities: ['ROLE_INFO_USERCOST'],
                pageTitle: 'cpmApp.userCost.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/info/user-cost/user-cost-detail.html',
                    controller: 'UserCostDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    return $ocLazyLoad.load(['app/info/user-cost/user-cost-detail.controller.js']);
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('userCost');
                    $translatePartialLoader.addPart('deptInfo');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', '$ocLazyLoad','$injector', function($stateParams, $ocLazyLoad,$injector) {
                	return $ocLazyLoad.load('app/info/user-cost/user-cost.service.js').then(
                			function(){
                				return $injector.get('UserCost').get({id : $stateParams.id}).$promise;
                			}
                	);
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'user-cost',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('user-cost.new', {
            parent: 'user-cost',
            url: '/new',
            pageTitle: 'cpmApp.userCost.home.createOrEditLabel',
            data: {
                authorities: ['ROLE_INFO_USERCOST']
            },
            views: {
                'content@': {
                    templateUrl: 'app/info/user-cost/user-cost-dialog.html',
                    controller: 'UserCostDialogController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    return $ocLazyLoad.load('app/info/user-cost/user-cost-dialog.controller.js');
                }],
            	translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('userCost');
                    $translatePartialLoader.addPart('deptInfo');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
                entity: function () {
                    return {
	                    userId: null,
	                    costMonth: null,
	                    internalCost: null,
	                    externalCost: null,
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
                		queryDept:'user-cost.new.queryDept',
            			name: $state.current.name || 'user-cost',
            			params: $state.params,
            			url: $state.href($state.current.name, $state.params)
                	};
                	return currentStateData;
	            }]
            }
        })
        .state('user-cost.new.queryDept', {
            parent: 'user-cost.new',
            url: '/queryDept?selectType&showChild',
            data: {
                authorities: ['ROLE_INFO_USERCOST']
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
                            return $ocLazyLoad.load([
                                                     'app/info/dept-info/dept-info-query.controller.js',
                                                     'app/info/dept-info/dept-info.service.js'
                                                     ]);
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
        .state('user-cost.edit', {
            parent: 'user-cost',
            url: '/edit/{id}',
            data: {
                authorities: ['ROLE_INFO_USERCOST']
            },
            views: {
                'content@': {
                    templateUrl: 'app/info/user-cost/user-cost-dialog.html',
                    controller: 'UserCostDialogController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    return $ocLazyLoad.load('app/info/user-cost/user-cost-dialog.controller.js');
                }],
            	translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('userCost');
                    $translatePartialLoader.addPart('deptInfo');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', '$ocLazyLoad','$injector', function($stateParams, $ocLazyLoad,$injector) {
	            	return $ocLazyLoad.load('app/info/user-cost/user-cost.service.js').then(
	            			function(){
	            				return $injector.get('UserCost').get({id : $stateParams.id}).$promise;
	            			}
	            	);
	            }],
                previousState: ["$state", function ($state) {
                	var currentStateData = {
                		queryDept:'user-cost.edit.queryDept',
            			name: $state.current.name || 'user-cost',
            			params: $state.params,
            			url: $state.href($state.current.name, $state.params)
                	};
                	return currentStateData;
	            }]
            }
        })
        .state('user-cost.edit.queryDept', {
            parent: 'user-cost.edit',
            url: '/queryDept?selectType&showChild',
            data: {
                authorities: ['ROLE_INFO_USERCOST']
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
                            return $ocLazyLoad.load([
                                                     'app/info/dept-info/dept-info-query.controller.js',
                                                     'app/info/dept-info/dept-info.service.js'
                                                     ]);
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
        .state('user-cost.delete', {
            parent: 'user-cost',
            url: '/delete/{id}',
            data: {
                authorities: ['ROLE_INFO_USERCOST']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/info/user-cost/user-cost-delete-dialog.html',
                    controller: 'UserCostDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                    	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                            return $ocLazyLoad.load('app/info/user-cost/user-cost-delete-dialog.controller.js');
                        }],
                        entity: ['UserCost', function(UserCost) {
                            return UserCost.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('user-cost', null, { reload: 'user-cost' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('user-cost.upload', {
            parent: 'user-cost',
            url: '/upload',
            data: {
                authorities: ['ROLE_INFO_USERCOST'],
            },
            views: {
                'content@': {
                    templateUrl: 'app/info/user-cost/user-cost-upload.html',
                    controller: 'UserCostUploadController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    return $ocLazyLoad.load('app/info/user-cost/user-cost-upload.controller.js');
                }],
            	translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('userCost');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
                previousState: ["$state", function ($state) {
                	var currentStateData = {
            			name: $state.current.name || 'user-cost',
            			params: $state.params,
            			url: $state.href($state.current.name, $state.params)
                	};
                	return currentStateData;
	            }]
            }
        });
    }

})();
