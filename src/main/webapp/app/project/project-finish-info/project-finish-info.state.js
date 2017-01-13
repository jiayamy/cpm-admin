(function() {
    'use strict';

    angular
        .module('cpmApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('project-finish-info', {
            parent: 'project',
            url: '/project-finish-info?page&sort&search',
            data: {
                authorities: ['ROLE_PROJECT_FINISH'],
                pageTitle: 'cpmApp.projectFinishInfo.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/project/project-finish-info/project-finish-infos.html',
                    controller: 'ProjectFinishInfoController',
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
                search: null
            },
            resolve: {
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
                    $translatePartialLoader.addPart('projectFinishInfo');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('project-finish-info-detail', {
            parent: 'project',
            url: '/project-finish-info/{id}',
            data: {
                authorities: ['ROLE_PROJECT_FINISH'],
                pageTitle: 'cpmApp.projectFinishInfo.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/project/project-finish-info/project-finish-info-detail.html',
                    controller: 'ProjectFinishInfoDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('projectFinishInfo');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'ProjectFinishInfo', function($stateParams, ProjectFinishInfo) {
                    return ProjectFinishInfo.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'project-finish-info',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('project-finish-info-detail.edit', {
            parent: 'project-finish-info-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_PROJECT_FINISH']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/project/project-finish-info/project-finish-info-dialog.html',
                    controller: 'ProjectFinishInfoDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['ProjectFinishInfo', function(ProjectFinishInfo) {
                            return ProjectFinishInfo.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('project-finish-info.new', {
            parent: 'project-finish-info',
            url: '/new',
            data: {
                authorities: ['ROLE_PROJECT_FINISH']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/project/project-finish-info/project-finish-info-dialog.html',
                    controller: 'ProjectFinishInfoDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                projectId: null,
                                finishRate: null,
                                creator: null,
                                createTime: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('project-finish-info', null, { reload: 'project-finish-info' });
                }, function() {
                    $state.go('project-finish-info');
                });
            }]
        })
        .state('project-finish-info.edit', {
            parent: 'project-finish-info',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_PROJECT_FINISH']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/project/project-finish-info/project-finish-info-dialog.html',
                    controller: 'ProjectFinishInfoDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['ProjectFinishInfo', function(ProjectFinishInfo) {
                            return ProjectFinishInfo.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('project-finish-info', null, { reload: 'project-finish-info' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('project-finish-info.delete', {
            parent: 'project-finish-info',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_PROJECT_FINISH']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/project/project-finish-info/project-finish-info-delete-dialog.html',
                    controller: 'ProjectFinishInfoDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['ProjectFinishInfo', function(ProjectFinishInfo) {
                            return ProjectFinishInfo.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('project-finish-info', null, { reload: 'project-finish-info' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
