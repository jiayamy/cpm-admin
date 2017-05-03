(function() {
    'use strict';

    angular
        .module('cpmApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('project-info', {
            parent: 'project',
            url: '/project-info?contractId&serialNum&name&status',
            data: {
                authorities: ['ROLE_PROJECT_INFO'],
                pageTitle: 'cpmApp.projectInfo.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/project/project-info/project-infos.html',
                    controller: 'ProjectInfoController',
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
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    return $ocLazyLoad.load([
                                             'app/info/dept-info/dept-info.service.js',
                                             'app/project/project-info/project-info.service.js',
                                             'app/project/project-info/project-info.controller.js']);
                }],
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        contractId: $stateParams.contractId,
                        serialNum: $stateParams.serialNum,
                        name: $stateParams.name,
                        status: $stateParams.status
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('projectInfo');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('project-info-detail', {
            parent: 'project-info',
            url: '/detail/{id}',
            data: {
                authorities: ['ROLE_PROJECT_INFO'],
                pageTitle: 'cpmApp.projectInfo.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/project/project-info/project-info-detail.html',
                    controller: 'ProjectInfoDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    return $ocLazyLoad.load('app/project/project-info/project-info-detail.controller.js');
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('projectInfo');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', '$ocLazyLoad','$injector', function($stateParams, $ocLazyLoad,$injector) {
                	return $ocLazyLoad.load('app/project/project-info/project-info.service.js').then(
                			function(){
                				return $injector.get('ProjectInfo').get({id : $stateParams.id}).$promise;
                			}
                	);
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'project-info',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('project-info-detail.edit', {
            parent: 'project-info-detail',
            url: '/edit',
            data: {
                authorities: ['ROLE_PROJECT_INFO'],
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
                entity: ['$stateParams', '$ocLazyLoad','$injector', function($stateParams, $ocLazyLoad,$injector) {
                	return $ocLazyLoad.load('app/project/project-info/project-info.service.js').then(
                			function(){
                				return $injector.get('ProjectInfo').get({id : $stateParams.id}).$promise;
                			}
                	);
                }],
                budgetEntity:function(){
                	return null;
                },
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                    	queryDept:'project-info-detail.edit.queryDept',
                        name: $state.current.name || 'project-info-detail',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('project-info-detail.edit.queryDept', {
            parent: 'project-info-detail.edit',
            url: '/queryDept?selectType&showChild',
            data: {
                authorities: ['ROLE_PROJECT_INFO']
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
        .state('project-info.new', {
            parent: 'project-info',
            url: '/new',
            data: {
                authorities: ['ROLE_PROJECT_INFO'],
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
                budgetEntity:function(){
                	return null;
                },
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                    	queryDept:'project-info.new.queryDept',
                        name: $state.current.name || 'project-info',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('project-info.new.queryDept', {
            parent: 'project-info.new',
            url: '/queryDept?selectType&showChild',
            data: {
                authorities: ['ROLE_PROJECT_INFO']
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
        .state('project-info.edit', {
            parent: 'project-info',
            url: '/edit/{id}',
            data: {
                authorities: ['ROLE_PROJECT_INFO'],
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
                entity: ['$stateParams', '$ocLazyLoad','$injector', function($stateParams, $ocLazyLoad,$injector) {
                	return $ocLazyLoad.load('app/project/project-info/project-info.service.js').then(
                			function(){
                				return $injector.get('ProjectInfo').get({id : $stateParams.id}).$promise;
                			}
                	);
                }],
                budgetEntity:function(){
                	return null;
                },
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                    	queryDept:'project-info.edit.queryDept',
                        name: $state.current.name || 'project-info',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('project-info.edit.queryDept', {
            parent: 'project-info.edit',
            url: '/queryDept?selectType&showChild',
            data: {
                authorities: ['ROLE_PROJECT_INFO']
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
        .state('project-info.delete', {
            parent: 'project-info',
            url: '/delete/{id}',
            data: {
                authorities: ['ROLE_PROJECT_INFO']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/project/project-info/project-info-delete-dialog.html',
                    controller: 'ProjectInfoDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                    	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                            return $ocLazyLoad.load('app/project/project-info/project-info-delete-dialog.controller.js');
                        }],
                        entity: ['ProjectInfo', function(ProjectInfo) {
                            return ProjectInfo.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('project-info', null, { reload: 'project-info' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('project-info.end', {
            parent: 'project-info',
            url: '/end/{id}',
            data: {
                authorities: ['ROLE_PROJECT_INFO']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/project/project-info/project-info-end-dialog.html',
                    controller: 'ProjectInfoEndController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                    	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                            return $ocLazyLoad.load('app/project/project-info/project-info-end-dialog.controller.js');
                        }],
                        entity: ['ProjectInfo', function(ProjectInfo) {
                            return ProjectInfo.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('project-info', null, { reload: 'project-info' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('project-info.finish', {
            parent: 'project-info',
            url: '/finish/{id}',
            data: {
                authorities: ['ROLE_PROJECT_INFO']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/project/project-info/project-info-finish-dialog.html',
                    controller: 'ProjectInfoFinishController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                    	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                            return $ocLazyLoad.load('app/project/project-info/project-info-finish-dialog.controller.js');
                        }],
                        entity: ['ProjectInfo', function(ProjectInfo) {
                            return ProjectInfo.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('project-info', null, { reload: 'project-info' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('project-info.import', {
            parent: 'project-info',
            url: '/upload',
            data: {
                authorities: ['ROLE_PROJECT_INFO'],
            },
            views: {
                'content@': {
                    templateUrl: 'app/project/project-info/project-info-upload.html',
                    controller: 'ProjectInfoUploadController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    return $ocLazyLoad.load('app/project/project-info/project-info-upload.controller.js');
                }],
            	translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('projectInfo');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
                previousState: ["$state", function ($state) {
                	var currentStateData = {
            			name: $state.current.name || 'project-info',
            			params: $state.params,
            			url: $state.href($state.current.name, $state.params)
                	};
                	return currentStateData;
	            }]
            }
        });
    }

})();
