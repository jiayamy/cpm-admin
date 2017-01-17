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
            url: '/user-cost?page&sort&userId&userName&costMonth&status',
            data: {
                authorities: ['ROLE_USER'],
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
                    value: 'id,asc',
                    squash: true
                },
//                search: null
                userId:null,
                userName:null,
                costMonth:null,
                status:null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
//                        search: $stateParams.search
                        userId:$stateParams.userId,
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
            parent: 'info',
            url: '/user-cost/{id}',
            data: {
                authorities: ['ROLE_USER'],
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
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('userCost');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'UserCost', function($stateParams, UserCost) {
                    return UserCost.get({id : $stateParams.id}).$promise;
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
        .state('user-cost-detail.edit', {
            parent: 'user-cost-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            views: {
                'content@': {
                    templateUrl: 'app/info/user-cost/user-cost-dialog.html',
                    controller: 'UserCostDialogController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
	            translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
	                $translatePartialLoader.addPart('userCost');
	                return $translate.refresh();
	            }],
	            entity: ['$stateParams', 'UserCost', function($stateParams, UserCost) {
	                return UserCost.get({id : $stateParams.id}).$promise;
	            }],
	            previousState: ["$state", function ($state) {
	                var currentStateData = {
	                    name: $state.current.name || 'user-cost-detail',
	                    params: $state.params,
	                    url: $state.href($state.current.name, $state.params)
	                };
	                return currentStateData;
	            }]
            }
//            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
//                $uibModal.open({
//                    templateUrl: 'app/info/user-cost/user-cost-dialog.html',
//                    controller: 'UserCostDialogController',
//                    controllerAs: 'vm',
//                    backdrop: 'static',
//                    size: 'lg',
//                    resolve: {
//                        entity: ['UserCost', function(UserCost) {
//                            return UserCost.get({id : $stateParams.id}).$promise;
//                        }]
//                    }
//                }).result.then(function() {
//                    $state.go('^', {}, { reload: false });
//                }, function() {
//                    $state.go('^');
//                });
//            }]
        })
        .state('user-cost.new', {
            parent: 'user-cost',
            url: '/new',
            pageTitle: 'cpmApp.userCost.home.createOrEditLabel',
            data: {
                authorities: ['ROLE_USER']
            },
            views: {
                'content@': {
                    templateUrl: 'app/info/user-cost/user-cost-dialog.html',
                    controller: 'UserCostDialogController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            	translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('userCost');
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
                }
            }
        })
        .state('user-cost.edit', {
            parent: 'user-cost',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            views: {
                'content@': {
                    templateUrl: 'app/info/user-cost/user-cost-dialog.html',
                    controller: 'UserCostDialogController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            	translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('userCost');
                    return $translate.refresh();
                }],
                entity: ['$stateParams','UserCost', function($stateParams,UserCost) {
                  return UserCost.get({id : $stateParams.id}).$promise;
                  }],
//                previousState: ["$state", function ($state) {
//                	var currentStateData = {
//            			name: $state.current.name || 'user-cost-detail',
//            			params: $state.params,
//            			url: $state.href($state.current.name, $state.params)
//                	};
//                	return currentStateData;
//	            }]
            }
//            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
//                $uibModal.open({
//                    templateUrl: 'app/info/user-cost/user-cost-dialog.html',
//                    controller: 'UserCostDialogController',
//                    controllerAs: 'vm',
//                    backdrop: 'static',
//                    size: 'lg',
//                    resolve: {
//                        entity: ['UserCost', function(UserCost) {
//                            return UserCost.get({id : $stateParams.id}).$promise;
//                        }]
//                    }
//                }).result.then(function() {
//                    $state.go('user-cost', null, { reload: 'user-cost' });
//                }, function() {
//                    $state.go('^');
//                });
//            }]
        })
        .state('user-cost.delete', {
            parent: 'user-cost',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/info/user-cost/user-cost-delete-dialog.html',
                    controller: 'UserCostDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
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
        });
    }

})();
