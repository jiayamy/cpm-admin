(function() {
    'use strict';

    angular
        .module('cpmApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('sales-annualIndex', {
            parent: 'info',
            url: '/sales-annualIndex?page&sort&statYear&userId&userName',
            data: {
                authorities: ['ROLE_INFO_BASIC'],
                pageTitle: 'cpmApp.salesAnnualIndex.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/info/sales-annualIndex/sales-annualIndexs.html',
                    controller: 'SalesAnnualIndexController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'wsai.statYear,desc',
                    squash: true
                },
                statYear: null,
                userId: null,
                userName: null
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    return $ocLazyLoad.load([
                                             'app/info/sales-annualIndex/sales-annualIndex.service.js',
                                             'app/info/sales-annualIndex/sales-annualIndex.controller.js'
                                             ]);
                }],
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        statYear: $stateParams.statYear,
                        userId: $stateParams.userId,
                        userName: $stateParams.userName
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('salesAnnualIndex');
                    $translatePartialLoader.addPart('global');
                    $translatePartialLoader.addPart('deptInfo');
                    return $translate.refresh();
                }]
            }
        })
        .state('sales-annualIndex.queryDept', {
            parent: 'sales-annualIndex',
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
                    	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                            return $ocLazyLoad.load([
                                                     'app/info/dept-info/dept-info-query.controller.js',
                                                     'app/info/dept-info/dept-info.service.js'
                                                     ]);
                        }],
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
        .state('sales-annualIndex.new', {
            parent: 'sales-annualIndex',
            url: '/new',
            data: {
                authorities: ['ROLE_INFO_BASIC'],
                pageTitle: 'cpmApp.salesAnnualIndex.home.createOrEditLabel'
            },
            views: {
                'content@': {
                    templateUrl: 'app/info/sales-annualIndex/sales-annualIndex-dialog.html',
                    controller: 'SalesAnnualIndexDialogController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    return $ocLazyLoad.load('app/info/sales-annualIndex/sales-annualIndex-dialog.controller.js');
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                	$translatePartialLoader.addPart('salesAnnualIndex');
                	$translatePartialLoader.addPart('deptInfo');
                	$translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
                entity: function () {
                    return {
                        id: null,
                        grade: null,
                        SalesAnnualIndex: 0,
                        socialSecurityFund: 0,
                        otherExpense: 0,
                        costBasis: 0,
                        hourCost: 0
                    };
                },
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                    	queryDept:'sales-annualIndex.new.queryDept',
                        name: $state.current.name || 'sales-annualIndex',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('sales-annualIndex.new.queryDept', {
            parent: 'sales-annualIndex.new',
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
                    	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                            return $ocLazyLoad.load([
                                                     'app/info/dept-info/dept-info-query.controller.js',
                                                     'app/info/dept-info/dept-info.service.js'
                                                     ]);
                        }],
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
        .state('sales-annualIndex.edit', {
            parent: 'sales-annualIndex',
            url: '/edit/{id}',
            data: {
                authorities: ['ROLE_INFO_BASIC'],
                pageTitle: 'cpmApp.salesAnnualIndex.home.createOrEditLabel'
            },
            views: {
                'content@': {
                    templateUrl: 'app/info/sales-annualIndex/sales-annualIndex-dialog.html',
                    controller: 'SalesAnnualIndexDialogController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    return $ocLazyLoad.load('app/info/sales-annualIndex/sales-annualIndex-dialog.controller.js');
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                	$translatePartialLoader.addPart('salesAnnualIndex');
                	$translatePartialLoader.addPart('deptInfo');
                	$translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', '$ocLazyLoad','$injector', function($stateParams, $ocLazyLoad,$injector) {
                	return $ocLazyLoad.load('app/info/sales-annualIndex/sales-annualIndex.service.js').then(
                			function(){
                				return $injector.get('SalesAnnualIndex').get({id : $stateParams.id}).$promise;
                			}
                	);
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                    	queryDept:'sales-annualIndex.edit.queryDept',
                        name: $state.current.name || 'sales-annualIndex',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        });
    }

})();
