(function() {
    'use strict';

    angular
        .module('cpmApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('contract-user', {
            parent: 'contract',
            url: '/contract-user?page&sort&search&contractId&userId&userName',
            data: {
                authorities: ['ROLE_CONTRACT_USER'],
                pageTitle: 'cpmApp.contractUser.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/contract/contract-user/contract-users.html',
                    controller: 'ContractUserController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'wcu.id,desc',
                    squash: true
                },
                contractId: null,
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
                        contractId: $stateParams.contractId,
                        userId: $stateParams.userId,
                        userName:$stateParams.userName
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('contractUser');
                    $translatePartialLoader.addPart('global');
                    $translatePartialLoader.addPart('deptInfo');
                    return $translate.refresh();
                }]
            }
        })
        .state('contract-user.queryDept', {
            parent: 'contract-user',
            url: '/queryDept?selectType&showChild',
            data: {
                authorities: ['ROLE_CONTRACT_USER']
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
        .state('contract-user-detail', {
            parent: 'contract-user',
            url: '/detail/{id}',
            data: {
                authorities: ['ROLE_CONTRACT_USER'],
                pageTitle: 'cpmApp.contractUser.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/contract/contract-user/contract-user-detail.html',
                    controller: 'ContractUserDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('contractUser');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'ContractUser', function($stateParams, ContractUser) {
                    return ContractUser.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'contract-user',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('contract-user-detail.edit', {
            parent: 'contract-user-detail',
            url: '/edit',
            data: {
                authorities: ['ROLE_CONTRACT_USER'],
                pageTitle: 'cpmApp.contractUser.home.createOrEditLabel'
            },
            views: {
                'content@': {
                	templateUrl: 'app/contract/contract-user/contract-user-dialog.html',
                    controller: 'ContractUserDialogController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                	$translatePartialLoader.addPart('contractUser');
                    $translatePartialLoader.addPart('deptInfo');
                    return $translate.refresh();
                }],
                entity: ['ContractUser','$stateParams', function(ContractUser,$stateParams) {
                    return ContractUser.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'contract-user',
                        queryDept:'contract-user-detail.edit.queryDept',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('contract-user-detail.edit.queryDept', {
            parent: 'contract-user-detail.edit',
            url: '/queryDept?selectType&showChild',
            data: {
                authorities: ['ROLE_CONTRACT_USER']
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
        .state('contract-user.new', {
            parent: 'contract-user',
            url: '/new',
            data: {
                authorities: ['ROLE_CONTRACT_USER'],
        		pageTitle: 'cpmApp.contractUser.detail.title'
            },
            views: {
                'content@': {
                	templateUrl: 'app/contract/contract-user/contract-user-dialog.html',
                    controller: 'ContractUserDialogController',
                    controllerAs: 'vm',
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                	$translatePartialLoader.addPart('contractUser');
                    $translatePartialLoader.addPart('deptInfo');
                    return $translate.refresh();
                }],
                entity: function () {
                	return {
                		contractId: null,
                        userId: null,
                        userName: null,
                        deptId: null,
                        joinDay: null,
                        leaveDay: null,
                        creator: null,
                        createTime: null,
                        updator: null,
                        updateTime: null,
                        id: null
                    };
                },
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'contract-user',
                        queryDept:'contract-user.new.queryDept',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('contract-user.new.queryDept', {
            parent: 'contract-user.new',
            url: '/queryDept?selectType&showChild',
            data: {
                authorities: ['ROLE_CONTRACT_USER']
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
        .state('contract-user.edit', {
            parent: 'contract-user',
            url: '/edit/{id}',
            data: {
                authorities: ['ROLE_CONTRACT_USER'],
        		pageTitle: 'cpmApp.contractUser.home.createOrEditLabel'
            },
            views: {
                'content@': {
	            	templateUrl: 'app/contract/contract-user/contract-user-dialog.html',
	            	controller: 'ContractUserDialogController',
	                controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                	$translatePartialLoader.addPart('contractUser');
                    $translatePartialLoader.addPart('deptInfo');
                    return $translate.refresh();
                }],
                entity: ['ContractUser','$stateParams', function(ContractUser,$stateParams) {
                    return ContractUser.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'contract-user',
                        queryDept:'contract-user.edit.queryDept',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('contract-user.edit.queryDept', {
            parent: 'contract-user.edit',
            url: '/queryDept?selectType&showChild',
            data: {
                authorities: ['ROLE_CONTRACT_USER']
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
        .state('contract-user.delete', {
            parent: 'contract-user',
            url: '/delete/{id}',
            data: {
                authorities: ['ROLE_CONTRACT_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/contract/contract-user/contract-user-delete-dialog.html',
                    controller: 'ContractUserDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['ContractUser', function(ContractUser) {
                            return ContractUser.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('contract-user', null, { reload: 'contract-user' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
