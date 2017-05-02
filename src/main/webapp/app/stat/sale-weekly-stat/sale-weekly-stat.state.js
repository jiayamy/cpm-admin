(function() {
    'use strict';

    angular
        .module('cpmApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('sale-weekly-stat', {
            parent: 'stat',
            url: '/sale-weekly-stat?page&deptId',
            data: {
                authorities: ['ROLE_STAT_SALE'],
                pageTitle: 'cpmApp.saleWeeklyStat.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/stat/sale-weekly-stat/sale-weekly-stats.html',
                    controller: 'SaleWeeklyStatController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 's.id,desc',
                    squash: true
                },
                deptId : null
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    return $ocLazyLoad.load([
                                             'app/stat/sale-weekly-stat/sale-weekly-stat.service.js',
                                             'app/stat/sale-weekly-stat/sale-weekly-stat.controller.js',
                                             'app/info/dept-info/dept-info.service.js']);
                }],
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        deptId: $stateParams.deptId
                        //deptName: $stateParams.deptName
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('saleWeeklyStat');
                    $translatePartialLoader.addPart('deptInfo');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('sale-weekly-stat.queryDept', {
            parent: 'sale-weekly-stat',
            url: '/queryDept?selectType&showChild&showUser',
            data: {
                authorities: ['ROLE_STAT_SALE']
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
        .state('sale-weekly-stat-detail', {
        	parent: 'sale-weekly-stat',
        	url: '/detail/{id}',
        	data: {
                authorities: ['ROLE_STAT_SALE'],
                pageTitle: 'cpmApp.saleWeeklyStat.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/stat/sale-weekly-stat/sale-weekly-stat-detail.html',
                    controller: 'SaleWeeklyStatDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    return $ocLazyLoad.load('app/stat/sale-weekly-stat/sale-weekly-stat-detail.controller.js');
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('saleWeeklyStat');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
	            entity: ['$stateParams', '$ocLazyLoad','$injector', function($stateParams, $ocLazyLoad,$injector) {
                	return $ocLazyLoad.load('app/stat/sale-weekly-stat/sale-weekly-stat.service.js').then(
                			function(){
                				return $injector.get('SaleWeeklyStat').get({id : $stateParams.id}).$promise;
                			}
                	);
                }],
	            previousState: ["$state", function ($state) {
	                var currentStateData = {
	                    name: $state.current.name || 'sale-weekly-stat',
	                    params: $state.params,
	                    url: $state.href($state.current.name, $state.params)
	                };
	                return currentStateData;
	            }]
            }
        })
        .state('sale-weekly-stat-chart', {
            parent: 'sale-weekly-stat',
            url: '/chart/{id}/queryChart?fromDate&toDate',
            data: {
                authorities: ['ROLE_STAT_SALE'],
                pageTitle: 'cpmApp.saleWeeklyStat.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/stat/sale-weekly-stat/sale-weekly-stat-chart.html',
                    controller: 'SaleWeeklyStatChartController',
                    controllerAs: 'vm'
                }
            },
            params: {
                fromDate: null,
                toDate : null,
                id : null
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    return $ocLazyLoad.load('app/stat/sale-weekly-stat/sale-weekly-stat-chart.controller.js');
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                	$translatePartialLoader.addPart('saleWeeklyStat');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        fromDate: $stateParams.fromDate,
                        toDate : $stateParams.toDate,
                        id : $stateParams.id
                    };
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: 'sale-weekly-stat',
                        params: {
                            page: {
                                value: '1',
                                squash: true
                            },
                            sort: {
                                value: 's.id,desc',
                                squash: true
                            },
                            deptId: null
                        },
                        url: $state.href('sale-weekly-stat', {
                            page: {
                                value: '1',
                                squash: true
                            },
                            sort: {
                                value: 's.id,desc',
                                squash: true
                            },
                            deptId: null
                        })
                    };
                    return currentStateData;
                }]
                
            }
        })
    }
})();
