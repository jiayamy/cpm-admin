(function() {
    'use strict';

    angular
        .module('cpmApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('dept-info', {
            parent: 'info',
            url: '/dept-info?page&sort&search',
            data: {
                authorities: ['ROLE_INFO_BASIC'],
                pageTitle: 'cpmApp.deptInfo.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/info/dept-info/dept-infos.html',
                    controller: 'DeptInfoController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,desc',
                    squash: true
                },
                search: null
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    return $ocLazyLoad.load([
                                             'app/info/dept-info/dept-info.service.js',
                                             'app/info/dept-info/dept-info.controller.js']);
                }],
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        search: $stateParams.search
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('deptInfo');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('dept-info-detail', {
            parent: 'dept-info',
            url: '/detail/{id}',
            data: {
                authorities: ['ROLE_INFO_BASIC'],
                pageTitle: 'cpmApp.deptInfo.detail.title'
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/info/dept-info/dept-info-detail.html',
                    controller: 'DeptInfoDetailController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                    	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                            return $ocLazyLoad.load('app/info/dept-info/dept-info-detail.controller.js');
                        }],
                        entity: ['DeptInfo', function(DeptInfo) {
                            return DeptInfo.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('dept-info', null, { reload: 'dept-info' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('dept-info.new', {
            parent: 'dept-info',
            url: '/new/{id}',
            data: {
                authorities: ['ROLE_INFO_BASIC']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/info/dept-info/dept-info-dialog.html',
                    controller: 'DeptInfoDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                    	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                            return $ocLazyLoad.load(['app/info/dept-info/dept-info-dialog.controller.js',
                                                     'app/info/dept-type/dept-type.service.js']);
                        }],
                    	entity:function(){
                        	return {
                        		parentId:$stateParams.id
                        	};
                        },
                        parentEntity: ['DeptInfo', function(DeptInfo) {
                            return DeptInfo.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('dept-info', null, { reload: 'dept-info' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('dept-info.edit', {
            parent: 'dept-info',
            url: '/edit/{id}',
            data: {
                authorities: ['ROLE_INFO_BASIC']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/info/dept-info/dept-info-dialog.html',
                    controller: 'DeptInfoDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                    	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                            return $ocLazyLoad.load(['app/info/dept-info/dept-info-dialog.controller.js',
                                                     'app/info/dept-type/dept-type.service.js']);
                        }],
                        entity: ['DeptInfo', function(DeptInfo) {
                            return DeptInfo.get({id : $stateParams.id}).$promise;
                        }],
                        parentEntity:function(){
                        	return {};
                        }
                    }
                }).result.then(function() {
                    $state.go('dept-info', null, { reload: 'dept-info' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('dept-info.delete', {
            parent: 'dept-info',
            url: '/delete/{id}',
            data: {
                authorities: ['ROLE_INFO_BASIC']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/info/dept-info/dept-info-delete-dialog.html',
                    controller: 'DeptInfoDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                    	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                            return $ocLazyLoad.load('app/info/dept-info/dept-info-delete-dialog.controller.js');
                        }],
                        entity: ['DeptInfo', function(DeptInfo) {
                            return DeptInfo.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('dept-info', null, { reload: 'dept-info' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
