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
                    value: 'id,asc',
                    squash: true
                },
                workDay: null,
                type:null,
                objName:null
            },
            resolve: {
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
            parent: 'timesheet',
            url: '/user-timesheet/{id}',
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
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('userTimesheet');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'UserTimesheet', function($stateParams, UserTimesheet) {
                    return UserTimesheet.get({id : $stateParams.id}).$promise;
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
        .state('user-timesheet-detail.edit', {
            parent: 'user-timesheet-detail',
            url: '/detail/edit/{id}',
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
	            translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
	                $translatePartialLoader.addPart('userTimesheet');
	                return $translate.refresh();
	            }],
	            entity: ['$stateParams', 'UserTimesheet', function($stateParams, UserTimesheet) {
	                return UserTimesheet.get({id : $stateParams.id}).$promise;
	            }],
	            previousState: ["$state", function ($state) {
	            	console.log($state.current.name);
	                var currentStateData = {
	                    name: $state.current.name || 'user-timesheet-detail',
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
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/timesheet/user-timesheet/user-timesheet-dialog.html',
                    controller: 'UserTimesheetDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
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
                        }
                    }
                }).result.then(function() {
                    $state.go('user-timesheet', null, { reload: 'user-timesheet' });
                }, function() {
                    $state.go('user-timesheet');
                });
            }]
        })
        .state('user-timesheet.edit', {
            parent: 'user-timesheet',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_TIMESHEET']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/timesheet/user-timesheet/user-timesheet-dialog.html',
                    controller: 'UserTimesheetDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
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
        })
        .state('user-timesheet.delete', {
            parent: 'user-timesheet',
            url: '/{id}/delete',
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
