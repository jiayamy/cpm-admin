(function() {
    'use strict';

    angular
        .module('cpmApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('project-overall-controller', {
            parent: 'stat',
            url: '/project-overall-controller?fromDate&toDate&contractId&userId&userName',
            data: {
                pageTitle: 'cpmApp.projectOverallController.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/stat/project-overall-controller/project-overall-controller.html',
                    controller: 'ProjectOverallController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'wpo.id,desc',
                    squash: true
                },
                fromDate: null,
                toDate: null,
                contractId: null,
                userId: null,
                userName: null,
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        fromDate: $stateParams.fromDate,
                        toDate: $stateParams.toDate,
                        contractId: $stateParams.contractId,
                        userId: $stateParams.userId,
                        userName: $stateParams.userName
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('projectOverallController');
                    $translatePartialLoader.addPart('global');
                    $translatePartialLoader.addPart('deptInfo');
                    return $translate.refresh();
                }]
            }
        })
        .state('project-overall-controller.queryDept', {
            parent: 'project-overall-controller',
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
        .state('project-overall-controller-detail', {
            parent: 'project-overall-controller',
            url: '/detail/{id}',
            data: {
                authorities: ['ROLE_PROJECT_USER'],
                pageTitle: 'cpmApp.projectUser.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/stat/project-overall-controller/project-overall-detail.html',
                    controller: 'ProjectOverallDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('projectOverallController');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'ProjectOverall', function($stateParams, ProjectOverall) {
                    return ProjectOverall.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'project-overall-controller',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
    }
})();
