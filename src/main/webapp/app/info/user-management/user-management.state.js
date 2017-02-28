(function() {
    'use strict';

    angular
        .module('cpmApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('user-management', {
            parent: 'info',
            url: '/user-management?page&sort&loginName&serialNum&lastName&workArea&deptId&deptName&grade',
            data: {
                authorities: ['ROLE_INFO_BASIC'],
                pageTitle: 'userManagement.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/info/user-management/user-management.html',
                    controller: 'UserManagementController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'user.id,desc',
                    squash: true
                },
                loginName:null,
                serialNum:null,
                lastName:null,
                deptId:null,
                deptName:null,
                workArea:null,
                grade:null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        loginName: $stateParams.loginName,
                        serialNum: $stateParams.serialNum,
                        lastName: $stateParams.lastName,
                        deptId: $stateParams.deptId,
                        deptName: $stateParams.deptName,
                        workArea: $stateParams.workArea,
                        grade: $stateParams.grade
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('user-management');
                    $translatePartialLoader.addPart('deptInfo');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]

            }        })
        .state('user-management.queryDept', {
            parent: 'user-management',
            url: '/queryDept?selectType&showChild&showUser',
            data: {
                authorities: ['ROLE_INFO_BASIC']
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
                            	showUser : $stateParams.showUser
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
        .state('user-management-detail', {
            parent: 'user-management',
            url: '/detail/:login',
            data: {
                authorities: ['ROLE_INFO_BASIC'],
                pageTitle: 'user-management.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/info/user-management/user-management-detail.html',
                    controller: 'UserManagementDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('user-management');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('user-management.new', {
            parent: 'user-management',
            url: '/new',
            data: {
                authorities: ['ROLE_INFO_BASIC']
            },
            views: {
                'content@': {
                    templateUrl: 'app/info/user-management/user-management-dialog.html',
                    controller: 'UserManagementDialogController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('user-management');
                    $translatePartialLoader.addPart('deptInfo');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
                entity: function () {
                    return {
                        id: null, login: null, firstName: null, lastName: null, email: null,
                        activated: true, langKey: null, createdBy: null, createdDate: null,
                        lastModifiedBy: null, lastModifiedDate: null, resetDate: null,
                        resetKey: null, authorities: null,gender:1
                    };
                },
             	previousState: ["$state", function ($state) {
  	                var currentStateData = {
  	                	queryDept:'user-management.new.queryDept',
  	                    name: $state.current.name || 'user-management',
  	                    params: $state.params,
  	                    url: $state.href($state.current.name, $state.params)
  	                };
  	                return currentStateData;
  	            }]
            }
        })
        .state('user-management.new.queryDept', {
            parent: 'user-management.new',
            url: '/queryDept?selectType&showChild&showUser',
            data: {
                authorities: ['ROLE_INFO_BASIC']
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
                            	showUser : $stateParams.showUser
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
        .state('user-management.edit', {
            parent: 'user-management',
            url: '/edit/{login}',
            data: {
                authorities: ['ROLE_INFO_BASIC']
            },
            views: {
                'content@': {
                    templateUrl: 'app/info/user-management/user-management-dialog.html',
                    controller: 'UserManagementDialogController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('user-management');
                    $translatePartialLoader.addPart('deptInfo');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
                entity: ['$stateParams','User', function($stateParams,User) {
                    return User.get({login : $stateParams.login}).$promise;
                 }],
                 previousState: ["$state", function ($state) {
  	                var currentStateData = {
  	                	queryDept:'user-management.edit.queryDept',
  	                    name: $state.current.name || 'user-management',
  	                    params: $state.params,
  	                    url: $state.href($state.current.name, $state.params)
  	                };
  	                return currentStateData;
  	            }]
            }
        })
         .state('user-management.edit.queryDept', {
            parent: 'user-management.edit',
            url: '/queryDept?selectType&showChild&showUser',
            data: {
                authorities: ['ROLE_INFO_BASIC']
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
                            	showUser : $stateParams.showUser
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
        .state('user-management.delete', {
            parent: 'user-management',
            url: '/delete/{login}',
            data: {
                authorities: ['ROLE_INFO_BASIC']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/info/user-management/user-management-delete-dialog.html',
                    controller: 'UserManagementDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['User', function(User) {
                            return User.get({login : $stateParams.login});
                        }]
                    }
                }).result.then(function() {
                    $state.go('user-management', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }
})();
