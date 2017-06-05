(function() {
    'use strict';

    angular
        .module('cpmApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('user-timesheet', {
            parent: 'timesheet',
            url: '/user-timesheet?page&sort&workDay&type&objName',
            data: {
                authorities: ['ROLE_TIMESHEET'],
                pageTitle: 'cpmApp.userTimesheet.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/timesheet/user-timesheet/user-timesheets.html',
                    controller: 'UserTimesheetController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'workDay,desc',
                    squash: true
                },
                workDay: null,
                type:null,
                objName:null
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    return $ocLazyLoad.load([
                                             'app/timesheet/user-timesheet/user-timesheet.service.js',
                                             'app/timesheet/user-timesheet/user-timesheet.controller.js',
                                             'app/timesheet/user-timesheet/user-timesheet.search.service.js',
                                             "app/info/work-area/work-area.service.js"]);
                }],
                pagingParams: ["$state",'$stateParams', 'PaginationUtil', function ($state,$stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        workDay: $stateParams.workDay,
                        type:$stateParams.type,
                        objName:$stateParams.objName
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('userTimesheet');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('user-timesheet-detail', {
            parent: 'user-timesheet',
            url: '/detail/{id}',
            data: {
                authorities: ['ROLE_TIMESHEET'],
                pageTitle: 'cpmApp.userTimesheet.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/timesheet/user-timesheet/user-timesheet-detail.html',
                    controller: 'UserTimesheetDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
            		return $ocLazyLoad.load('app/timesheet/user-timesheet/user-timesheet-detail.controller.js');
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('userTimesheet');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', '$ocLazyLoad','$injector', function($stateParams, $ocLazyLoad,$injector) {
                	return $ocLazyLoad.load('app/timesheet/user-timesheet/user-timesheet.service.js').then(
                			function(){
                				return $injector.get('UserTimesheet').get({id : $stateParams.id}).$promise;
                			}
                	);
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'user-timesheet',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('user-timesheet.new', {
            parent: 'user-timesheet',
            url: '/new',
            data: {
                authorities: ['ROLE_TIMESHEET']
            },
            views: {
                'content@': {
                    templateUrl: 'app/timesheet/user-timesheet/user-timesheet-dialog.html',
                    controller: 'UserTimesheetDialogController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
            		return $ocLazyLoad.load('app/timesheet/user-timesheet/user-timesheet-dialog.controller.js');
                }],
	            translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
	                $translatePartialLoader.addPart('userTimesheet');
	                $translatePartialLoader.addPart('global');
	                return $translate.refresh();
	            }],
	            entity: ['$stateParams',  function($stateParams) {
	            	return {
                        workDay: null,
                        userId: null,
                        type: null,
                        objId: null,
                        objName: null,
                        realInput: null,
                        acceptInput: null,
                        status: null,
                        creator: null,
                        createTime: null,
                        updator: null,
                        updateTime: null,
                        id: null
                    };
	            }],
	            previousState: ["$state", function ($state) {
	                var currentStateData = {
	                    name: $state.current.name || 'user-timesheet',
	                    params: $state.params,
	                    url: $state.href($state.current.name, $state.params)
	                };
	                return currentStateData;
	            }]
            }
        })
        .state('user-timesheet.edit', {
            parent: 'user-timesheet',
            url: '/edit/{id}',
            data: {
                authorities: ['ROLE_TIMESHEET']
            },
            views: {
                'content@': {
                    templateUrl: 'app/timesheet/user-timesheet/user-timesheet-dialog.html',
                    controller: 'UserTimesheetDialogController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
            		return $ocLazyLoad.load('app/timesheet/user-timesheet/user-timesheet-dialog.controller.js');
                }],
	            translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
	                $translatePartialLoader.addPart('userTimesheet');
	                $translatePartialLoader.addPart('global');
	                return $translate.refresh();
	            }],
	            entity: ['$stateParams', '$ocLazyLoad','$injector', function($stateParams, $ocLazyLoad,$injector) {
                	return $ocLazyLoad.load('app/timesheet/user-timesheet/user-timesheet.service.js').then(
                			function(){
                				return $injector.get('UserTimesheet').get({id : $stateParams.id}).$promise;
                			}
                	);
                }],
	            previousState: ["$state", function ($state) {
	                var currentStateData = {
	                    name: $state.current.name || 'user-timesheet',
	                    params: $state.params,
	                    url: $state.href($state.current.name, $state.params)
	                };
	                return currentStateData;
	            }]
            }
        })
        .state('user-timesheet.delete', {
            parent: 'user-timesheet',
            url: '/delete/{id}',
            data: {
                authorities: ['ROLE_TIMESHEET']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/timesheet/user-timesheet/user-timesheet-delete-dialog.html',
                    controller: 'UserTimesheetDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                    	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    		return $ocLazyLoad.load('app/timesheet/user-timesheet/user-timesheet-delete-dialog.controller.js');
                        }],
                        entity: ['UserTimesheet', function(UserTimesheet) {
                            return UserTimesheet.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('user-timesheet', null, { reload: 'user-timesheet' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
