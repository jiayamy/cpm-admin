(function() {
    'use strict';

    angular
        .module('cpmApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('project-user', {
            parent: 'project',
            url: '/project-user?page&sort&projectId&userId&userName',
            data: {
                authorities: ['ROLE_PROJECT_USER'],
                pageTitle: 'cpmApp.projectUser.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/project/project-user/project-users.html',
                    controller: 'ProjectUserController',
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
                projectId: null,
                userId: null,
                userName:null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        projectId: $stateParams.projectId,
                        userId: $stateParams.userId,
                        userName:$stateParams.userName
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('projectUser');
                    $translatePartialLoader.addPart('global');
                    $translatePartialLoader.addPart('deptInfo');
                    return $translate.refresh();
                }]
            }
        })
        .state('project-user.queryDept', {
            parent: 'project-user',
            url: '/queryDept?selectType&showChild',
            data: {
                authorities: ['ROLE_PROJECT_USER']
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
        .state('project-user-detail', {
            parent: 'project-user',
            url: '/detail/{id}',
            data: {
                authorities: ['ROLE_PROJECT_USER'],
                pageTitle: 'cpmApp.projectUser.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/project/project-user/project-user-detail.html',
                    controller: 'ProjectUserDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('projectUser');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'ProjectUser', function($stateParams, ProjectUser) {
                    return ProjectUser.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'project-user',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('project-user-detail.edit', {
            parent: 'project-user-detail',
            url: '/edit',
            data: {
                authorities: ['ROLE_PROJECT_USER'],
                pageTitle: 'cpmApp.projectUser.home.createOrEditLabel'
            },
            views: {
                'content@': {
                    templateUrl: 'app/project/project-user/project-user-dialog.html',
                    controller: 'ProjectUserDialogController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('projectUser');
                    $translatePartialLoader.addPart('deptInfo');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'ProjectUser', function($stateParams, ProjectUser) {
                    return ProjectUser.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'project-user-detail',
                        queryDept:'project-user-detail.edit.queryDept',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('project-user-detail.edit.queryDept', {
            parent: 'project-user-detail.edit',
            url: '/queryDept?selectType&showChild',
            data: {
                authorities: ['ROLE_PROJECT_USER']
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
        .state('project-user.new', {
            parent: 'project-user',
            url: '/new',
            data: {
                authorities: ['ROLE_PROJECT_USER'],
                pageTitle: 'cpmApp.projectUser.home.createOrEditLabel'
            },
            views: {
                'content@': {
                    templateUrl: 'app/project/project-user/project-user-dialog.html',
                    controller: 'ProjectUserDialogController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('projectUser');
                    $translatePartialLoader.addPart('deptInfo');
                    return $translate.refresh();
                }],
                entity: function () {
                	return {
                        projectId: null,
                        userId: null,
                        userName: null,
                        userRole: null,
                        joinDay: null,
                        goodbyeDay: null,
                        creator: null,
                        createTime: null,
                        updator: null,
                        updateTime: null,
                        id: null
                    };
                },
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'project-user',
                        queryDept:'project-user.new.queryDept',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('project-user.new.queryDept', {
            parent: 'project-user.new',
            url: '/queryDept?selectType&showChild',
            data: {
                authorities: ['ROLE_PROJECT_USER']
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
        .state('project-user.edit', {
            parent: 'project-user',
            url: '/edit/{id}',
            data: {
                authorities: ['ROLE_PROJECT_USER'],
                pageTitle: 'cpmApp.projectUser.home.createOrEditLabel'
            },
            views: {
                'content@': {
                    templateUrl: 'app/project/project-user/project-user-dialog.html',
                    controller: 'ProjectUserDialogController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('projectUser');
                    $translatePartialLoader.addPart('deptInfo');
                    return $translate.refresh();
                }],
                entity: ['ProjectUser','$stateParams', function(ProjectUser,$stateParams) {
                    return ProjectUser.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'project-user',
                        queryDept:'project-user.edit.queryDept',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('project-user.edit.queryDept', {
            parent: 'project-user.edit',
            url: '/queryDept?selectType&showChild',
            data: {
                authorities: ['ROLE_PROJECT_USER']
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
        .state('project-user.delete', {
            parent: 'project-user',
            url: '/delete/{id}',
            data: {
                authorities: ['ROLE_PROJECT_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/project/project-user/project-user-delete-dialog.html',
                    controller: 'ProjectUserDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['ProjectUser', function(ProjectUser) {
                            return ProjectUser.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('project-user', null, { reload: 'project-user' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
