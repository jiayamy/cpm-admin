(function() {
    'use strict';

    angular
        .module('cpmApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('project-cost', {
            parent: 'project',
            url: '/project-cost?page&sort&projectId&name&type',
            data: {
                authorities: ['ROLE_PROJECT_COST'],
                pageTitle: 'cpmApp.projectCost.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/project/project-cost/project-costs.html',
                    controller: 'ProjectCostController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'wpc.costDay,desc',
                    squash: true
                },
                projectId: null,
                type: null,
                name: null
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    return $ocLazyLoad.load([
                                             'app/project/project-info/project-info.service.js',
                                             'app/project/project-cost/project-cost.service.js',
                                             'app/project/project-cost/project-cost.controller.js']);
                }],
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        projectId: $stateParams.projectId,
                        type: $stateParams.type,
                        name: $stateParams.name
                    };
                }],
                pageType:function(){
                	return 2;
                },
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('projectCost');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('project-cost-timesheet', {
            parent: 'project',
            url: '/project-cost-timesheet?page&sort&projectId&name&type',
            data: {
                authorities: ['ROLE_PROJECT_COST'],
                pageTitle: 'cpmApp.projectCost.home.timesheetTitle'
            },
            views: {
                'content@': {
                    templateUrl: 'app/project/project-cost/project-costs.html',
                    controller: 'ProjectCostController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'wpc.costDay,desc',
                    squash: true
                },
                projectId: null,
                type: null,
                name: null
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    return $ocLazyLoad.load([
                                             'app/project/project-info/project-info.service.js',
                                             'app/project/project-cost/project-cost.service.js',
                                             'app/project/project-cost/project-cost.controller.js']);
                }],
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        projectId: $stateParams.projectId,
                        type: $stateParams.type,
                        name: $stateParams.name
                    };
                }],
                pageType:function(){
                	return 1;
                },
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('projectCost');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('project-cost-detail', {
            parent: 'project-cost',
            url: '/detail/{id}',
            data: {
                authorities: ['ROLE_PROJECT_COST'],
                pageTitle: 'cpmApp.projectCost.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/project/project-cost/project-cost-detail.html',
                    controller: 'ProjectCostDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
            		return $ocLazyLoad.load('app/project/project-cost/project-cost-detail.controller.js');
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('projectCost');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', '$ocLazyLoad','$injector', function($stateParams, $ocLazyLoad,$injector) {
                	return $ocLazyLoad.load('app/project/project-cost/project-cost.service.js').then(
                			function(){
                				return $injector.get('ProjectCost').get({id : $stateParams.id}).$promise;
                			}
                	);
                }],
                pageType:function(){
                	return 2;
                },
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'project-cost',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('project-cost-timesheet-detail', {
            parent: 'project-cost-timesheet',
            url: '/detail/{id}',
            data: {
                authorities: ['ROLE_PROJECT_COST'],
                pageTitle: 'cpmApp.projectCost.detail.timesheetTitle'
            },
            views: {
                'content@': {
                    templateUrl: 'app/project/project-cost/project-cost-detail.html',
                    controller: 'ProjectCostDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
            		return $ocLazyLoad.load('app/project/project-cost/project-cost-detail.controller.js');
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('projectCost');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', '$ocLazyLoad','$injector', function($stateParams, $ocLazyLoad,$injector) {
                	return $ocLazyLoad.load('app/project/project-cost/project-cost.service.js').then(
                			function(){
                				return $injector.get('ProjectCost').get({id : $stateParams.id}).$promise;
                			}
                	);
                }],
                pageType:function(){
                	return 1;
                },
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'project-cost-timesheet',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('project-cost.new', {
            parent: 'project-cost',
            url: '/new',
            data: {
                authorities: ['ROLE_PROJECT_COST'],
                pageTitle: 'cpmApp.projectCost.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/project/project-cost/project-cost-dialog.html',
                    controller: 'ProjectCostDialogController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
            		return $ocLazyLoad.load('app/project/project-cost/project-cost-dialog.controller.js');
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('projectCost');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
                entity: function () {
                    return {
                        projectId: null,
                        name: null,
                        type: null,
                        costDay:null,
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
                        name: $state.current.name || 'project-cost',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('project-cost.edit', {
            parent: 'project-cost',
            url: '/edit/{id}',
            data: {
                authorities: ['ROLE_PROJECT_COST'],
                pageTitle: 'cpmApp.projectCost.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/project/project-cost/project-cost-dialog.html',
                    controller: 'ProjectCostDialogController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
            		return $ocLazyLoad.load('app/project/project-cost/project-cost-dialog.controller.js');
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('projectCost');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', '$ocLazyLoad','$injector', function($stateParams, $ocLazyLoad,$injector) {
                	return $ocLazyLoad.load('app/project/project-cost/project-cost.service.js').then(
                			function(){
                				return $injector.get('ProjectCost').get({id : $stateParams.id}).$promise;
                			}
                	);
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'project-cost',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('project-cost.delete', {
            parent: 'project-cost',
            url: '/delete/{id}',
            data: {
                authorities: ['ROLE_PROJECT_COST']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/project/project-cost/project-cost-delete-dialog.html',
                    controller: 'ProjectCostDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                    	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    		return $ocLazyLoad.load('app/project/project-cost/project-cost-delete-dialog.controller.js');
                        }],
                        entity: ['ProjectCost', function(ProjectCost) {
                            return ProjectCost.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('project-cost', null, { reload: 'project-cost' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('project-cost.upload', {
            parent: 'project-cost',
            url: '/upload',
            data: {
                authorities: ['ROLE_PROJECT_COST'],
            },
            views: {
                'content@': {
                    templateUrl: 'app/project/project-cost/project-cost-upload.html',
                    controller: 'ProjectCostUploadController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
            		return $ocLazyLoad.load('app/project/project-cost/project-cost-upload.controller.js');
                }],
            	translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('projectCost');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
                previousState: ["$state", function ($state) {
                	var currentStateData = {
            			name: $state.current.name || 'project-cost',
            			params: $state.params,
            			url: $state.href($state.current.name, $state.params)
                	};
                	return currentStateData;
	            }]
            }
        });
    }

})();
