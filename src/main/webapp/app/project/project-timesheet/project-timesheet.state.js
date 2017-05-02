(function() {
    'use strict';

    angular
        .module('cpmApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('project-timesheet', {
            parent: 'project',
            url: '/project-timesheet?page&sort&workDay&projectId&userId&userName',
            data: {
                authorities: ['ROLE_PROJECT_TIMESHEET'],
                pageTitle: 'cpmApp.projectTimesheet.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/project/project-timesheet/project-timesheets.html',
                    controller: 'ProjectTimesheetController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'wut.workDay,desc',
                    squash: true
                },
                workDay: null,
                projectId: null,
                userId: null,
                userName: null
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    return $ocLazyLoad.load([
                                             'app/project/project-info/project-info.service.js',
                                             'app/info/dept-info/dept-info.service.js',
                                             'app/project/project-timesheet/project-timesheet.service.js',
                                             'app/project/project-timesheet/project-timesheet.controller.js']);
                }],
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        workDay: $stateParams.workDay,
                        projectId: $stateParams.projectId,
                        userId: $stateParams.userId,
                        userName: $stateParams.userName
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('projectTimesheet');
                    $translatePartialLoader.addPart('contractTimesheet');
                    $translatePartialLoader.addPart('deptInfo');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('project-timesheet.queryDept', {
            parent: 'project-timesheet',
            url: '/queryDept?selectType&showChild',
            data: {
                authorities: ['ROLE_PROJECT_TIMESHEET']
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
        .state('project-timesheet-detail', {
            parent: 'project-timesheet',
            url: '/detail/{id}',
            data: {
                authorities: ['ROLE_PROJECT_TIMESHEET'],
                pageTitle: 'cpmApp.projectTimesheet.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/project/project-timesheet/project-timesheet-detail.html',
                    controller: 'ProjectTimesheetDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
            		return $ocLazyLoad.load('app/project/project-timesheet/project-timesheet-detail.controller.js');
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('projectTimesheet');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'ProjectTimesheet', function($stateParams, ProjectTimesheet) {
                    return ProjectTimesheet.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'project-timesheet',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('project-timesheet-detail.edit', {
            parent: 'project-timesheet-detail',
            url: '/edit',
            data: {
                authorities: ['ROLE_PROJECT_TIMESHEET']
            },
            views: {
                'content@': {
                    templateUrl: 'app/project/project-timesheet/project-timesheet-dialog.html',
                    controller: 'ProjectTimesheetDialogController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
            		return $ocLazyLoad.load('app/project/project-timesheet/project-timesheet-dialog.controller.js');
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('projectTimesheet');
                    $translatePartialLoader.addPart('contractTimesheet');
                    $translatePartialLoader.addPart('userTimesheet');
                    $translatePartialLoader.addPart('global');
                    
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'ProjectTimesheet', function($stateParams, ProjectTimesheet) {
                    return ProjectTimesheet.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'project-timesheet-detail',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('project-timesheet.edit', {
            parent: 'project-timesheet',
            url: '/edit/{id}',
            data: {
                authorities: ['ROLE_PROJECT_TIMESHEET']
            },
            views: {
                'content@': {
                	templateUrl: 'app/project/project-timesheet/project-timesheet-dialog.html',
                    controller: 'ProjectTimesheetDialogController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
            		return $ocLazyLoad.load('app/project/project-timesheet/project-timesheet-dialog.controller.js');
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                	$translatePartialLoader.addPart('projectTimesheet');
                    $translatePartialLoader.addPart('contractTimesheet');
                    $translatePartialLoader.addPart('userTimesheet');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'ProjectTimesheet', function($stateParams, ProjectTimesheet) {
                    return ProjectTimesheet.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'project-timesheet',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        });
    }

})();
