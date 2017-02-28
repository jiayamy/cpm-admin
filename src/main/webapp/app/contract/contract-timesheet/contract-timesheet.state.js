(function() {
    'use strict';

    angular
        .module('cpmApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('contract-timesheet', {
            parent: 'contract',
            url: '/contract-timesheet?page&sort&workDay&contractId&userId&userName',
            data: {
                authorities: ['ROLE_CONTRACT_TIMESHEET'],
                pageTitle: 'cpmApp.contractTimesheet.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/contract/contract-timesheet/contract-timesheets.html',
                    controller: 'ContractTimesheetController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'wut.workDay,desc',
                    squash: true
                },
                workDay: null,
                contractId: null,
                userId: null,
                userName: null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        workDay: $stateParams.workDay,
                        contractId: $stateParams.contractId,
                        userId: $stateParams.userId,
                        userName: $stateParams.userName
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('contractTimesheet');
                    $translatePartialLoader.addPart('deptInfo');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('contract-timesheet.queryDept', {
            parent: 'contract-timesheet',
            url: '/queryDept?selectType&showChild',
            data: {
                authorities: ['ROLE_CONTRACT_TIMESHEET']
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
        .state('contract-timesheet-detail', {
            parent: 'contract-timesheet',
            url: '/detail/{id}',
            data: {
                authorities: ['ROLE_CONTRACT_TIMESHEET'],
                pageTitle: 'cpmApp.contractTimesheet.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/contract/contract-timesheet/contract-timesheet-detail.html',
                    controller: 'ContractTimesheetDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('contractTimesheet');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'ContractTimesheet', function($stateParams, ContractTimesheet) {
                    return ContractTimesheet.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'contract-timesheet',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('contract-timesheet-detail.edit', {
            parent: 'contract-timesheet-detail',
            url: '/edit',
            data: {
                authorities: ['ROLE_CONTRACT_TIMESHEET']
            },
            views: {
                'content@': {
                    templateUrl: 'app/contract/contract-timesheet/contract-timesheet-dialog.html',
                    controller: 'ContractTimesheetDialogController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('contractTimesheet');
                    $translatePartialLoader.addPart('userTimesheet');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'ContractTimesheet', function($stateParams, ContractTimesheet) {
                    return ContractTimesheet.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'contract-timesheet-detail',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('contract-timesheet.edit', {
            parent: 'contract-timesheet',
            url: '/edit/{id}',
            data: {
                authorities: ['ROLE_CONTRACT_TIMESHEET']
            },
            views: {
                'content@': {
                    templateUrl: 'app/contract/contract-timesheet/contract-timesheet-dialog.html',
                    controller: 'ContractTimesheetDialogController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('contractTimesheet');
                    $translatePartialLoader.addPart('userTimesheet');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'ContractTimesheet', function($stateParams, ContractTimesheet) {
                    return ContractTimesheet.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'contract-timesheet',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        });
    }

})();
