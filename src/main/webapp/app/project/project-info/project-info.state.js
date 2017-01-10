(function() {
    'use strict';

    angular
        .module('cpmApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('project-info', {
            parent: 'project',
            url: '/project-info?contractId&serialNum&name&status',
            data: {
                authorities: ['ROLE_PROJECT_INFO'],
                pageTitle: 'cpmApp.projectInfo.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/project/project-info/project-infos.html',
                    controller: 'ProjectInfoController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'wpi.id,desc',
                    squash: true
                }
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        contractId: $stateParams.contractId,
                        serialNum: $stateParams.serialNum,
                        name: $stateParams.name,
                        status: $stateParams.status
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('projectInfo');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('project-info-detail', {
            parent: 'project-info',
            url: '/{id}/detail',
            data: {
                authorities: ['ROLE_PROJECT_INFO'],
                pageTitle: 'cpmApp.projectInfo.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/project/project-info/project-info-detail.html',
                    controller: 'ProjectInfoDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('projectInfo');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'ProjectInfo', function($stateParams, ProjectInfo) {
                    return ProjectInfo.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'project-info',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('project-info-detail.edit', {
            parent: 'project-info-detail',
            url: '/detail/edit',
            data: {
                authorities: ['ROLE_PROJECT_INFO'],
                pageTitle: 'cpmApp.projectInfo.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/project/project-info/project-info-dialog.html',
                    controller: 'ProjectInfoDialogController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('projectInfo');
                    $translatePartialLoader.addPart('deptInfo');
                    return $translate.refresh();
                }],
                entity: ['ProjectInfo','$stateParams', function(ProjectInfo,$stateParams) {
                    return ProjectInfo.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                    	queryDept:'project-info-detail.edit.queryDept',
                        name: $state.current.name || 'project-info-detail',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('project-info-detail.edit.queryDept', {
            parent: 'project-info-detail.edit',
            url: '/queryDept?selectType&showChild',
            data: {
                authorities: ['ROLE_PROJECT_INFO']
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
        .state('project-info.new', {
            parent: 'project-info',
            url: '/new',
            data: {
                authorities: ['ROLE_PROJECT_INFO'],
                pageTitle: 'cpmApp.projectInfo.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/project/project-info/project-info-dialog.html',
                    controller: 'ProjectInfoDialogController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('projectInfo');
                    $translatePartialLoader.addPart('deptInfo');
                    return $translate.refresh();
                }],
                entity: function () {
                    return {
                        serialNum: null,
                        contractId: null,
                        budgetId: null,
                        name: null,
                        pm: null,
                        dept: null,
                        startDay: null,
                        endDay: null,
                        budgetTotal: null,
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
                    	queryDept:'project-info.new.queryDept',
                        name: $state.current.name || 'project-info',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('project-info.new.queryDept', {
            parent: 'project-info.new',
            url: '/queryDept?selectType&showChild',
            data: {
                authorities: ['ROLE_PROJECT_INFO']
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
        .state('project-info.edit', {
            parent: 'project-info',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_PROJECT_INFO'],
                pageTitle: 'cpmApp.projectInfo.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/project/project-info/project-info-dialog.html',
                    controller: 'ProjectInfoDialogController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('projectInfo');
                    $translatePartialLoader.addPart('deptInfo');
                    return $translate.refresh();
                }],
                entity: ['ProjectInfo','$stateParams', function(ProjectInfo,$stateParams) {
                    return ProjectInfo.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                    	queryDept:'project-info.edit.queryDept',
                        name: $state.current.name || 'project-info-detail',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('project-info.edit.queryDept', {
            parent: 'project-info.edit',
            url: '/queryDept?selectType&showChild',
            data: {
                authorities: ['ROLE_PROJECT_INFO']
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
        .state('project-info.delete', {
            parent: 'project-info',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_PROJECT_INFO']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/project/project-info/project-info-delete-dialog.html',
                    controller: 'ProjectInfoDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['ProjectInfo', function(ProjectInfo) {
                            return ProjectInfo.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('project-info', null, { reload: 'project-info' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('project-info.end', {
            parent: 'project-info',
            url: '/{id}/end',
            data: {
                authorities: ['ROLE_PROJECT_INFO']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/project/project-info/project-info-end-dialog.html',
                    controller: 'ProjectInfoEndController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['ProjectInfo', function(ProjectInfo) {
                            return ProjectInfo.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('project-info', null, { reload: 'project-info' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('project-info.finish', {
            parent: 'project-info',
            url: '/{id}/finish',
            data: {
                authorities: ['ROLE_PROJECT_INFO']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/project/project-info/project-info-finish-dialog.html',
                    controller: 'ProjectInfoFinishController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['ProjectInfo', function(ProjectInfo) {
                            return ProjectInfo.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('project-info', null, { reload: 'project-info' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
