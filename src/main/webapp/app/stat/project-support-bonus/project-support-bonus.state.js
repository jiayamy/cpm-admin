(function() {
    'use strict';

    angular
        .module('cpmApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('project-support-bonus', {
            parent: 'stat',
            url: '/project-support-bonus?&statWeek&contractId&deptType',
            data: {
                pageTitle: 'cpmApp.projectSupportBonus.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/stat/project-support-bonus/project-support-bonus.html',
                    controller: 'ProjectSupportBonusController',
                    controllerAs: 'vm'
                }
            },
            params: {
            	page: {
                    value: '1',
                    squash: true
                },
                statWeek: null,
                contractId: null,
                deptType: null
            },
            resolve: {
                pagingParams: ['$stateParams','PaginationUtil',function ($stateParams,PaginationUtil) {
                    return {
                    	page: PaginationUtil.parsePage($stateParams.page),
                        statWeek: $stateParams.statWeek,
                        contractId: $stateParams.contractId,
                        deptType: $stateParams.deptType
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('projectSupportBonus');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('project-support-bonus-detail', {
            parent: 'project-support-bonus',
            url: '/detail/{id}',
            data: {
                authorities: ['ROLE_PROJECT_USER'],
                pageTitle: 'cpmApp.projectSupportBonus.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/stat/project-support-bonus/project-support-bonus-detail.html',
                    controller: 'ProjectSupportBonusDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('projectSupportBonus');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'ProjectSupportBonus', function($stateParams, ProjectSupportBonus) {
                    return ProjectSupportBonus.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'project-support-bonus',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
    }
})();
