(function() {
    'use strict';

    angular
        .module('cpmApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('system-config', {
            parent: 'info',
            url: '/system-config?page&sort&search',
            data: {
                authorities: ['ROLE_INFO_BASIC'],
                pageTitle: 'cpmApp.systemConfig.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/info/system-config/system-config.html',
                    controller: 'SystemConfigController',
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
                    $translatePartialLoader.addPart('systemConfig');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('system-config.edit', {
            parent: 'system-config',
            url: '/edit/{id}',
            data: {
                authorities: ['ROLE_INFO_BASIC']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                	templateUrl: 'app/info/system-config/system-config-dialog.html',
                    controller: 'SystemConfigDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
		                entity: ['SystemConfig', function(SystemConfig) {
		                    return SystemConfig.get({id : $stateParams.id}).$promise;
		                }]
                    }
                }).result.then(function() {
                	 $state.go('system-config', null, { reload: 'system-config' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        
    }

})();
