(function() {
    'use strict';

    angular
        .module('cpmApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('share-cost-rate', {
            parent: 'info',
            url: '/share-cost-rate?page&sort&deptType&contractType',
            data: {
                authorities: ['ROLE_INFO_BASIC'],
                pageTitle: 'cpmApp.shareCostRate.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/info/share-cost-rate/share-cost-rates.html',
                    controller: 'ShareCostRateController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'wscr.contractType,desc',
                    squash: true
                },
                deptType: null,
                contractType: null
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    return $ocLazyLoad.load([
                                             'app/info/share-cost-rate/share-cost-rate.service.js',
                                             'app/info/share-cost-rate/share-cost-rate.controller.js',
                                             'app/info/dept-type/dept-type.service.js'
                                             ]);
                }],
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        deptType: $stateParams.deptType,
                        contractType: $stateParams.contractType
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('shareCostRate');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('share-cost-rate.new',{
        	parent: 'share-cost-rate',
            url: '/new',
            data: {
            	authorities: ['ROLE_INFO_BASIC']
            },
            views:{
            	'content@':{
            		templateUrl: 'app/info/share-cost-rate/share-cost-rate-dialog.html',
                    controller: 'ShareCostRateDialogController',
                    controllerAs: 'vm'
            	}
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    return $ocLazyLoad.load([
                                             'app/info/share-cost-rate/share-cost-rate-dialog.controller.js',
                                             'app/info/dept-type/dept-type.service.js'
                                             ]);
                }],
            	 translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                     $translatePartialLoader.addPart('shareCostRate');
                     $translatePartialLoader.addPart('global');
                     return $translate.refresh();
                 }],
                entity: function () {
                    return {
                        deptType: null,
                        contractType: null,
                        rate: null,
                        creator: null,
                        createTime: null,
                        updator: null,
                        updateTime: null,
                        id: null
                    };
                },
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'share-cost-rate',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('share-cost-rate.edit', {
            parent: 'share-cost-rate',
            url: '/edit/{id}',
            data: {
            	authorities: ['ROLE_INFO_BASIC']
            },
            views: {
            	'content@': {
                    templateUrl: 'app/info/share-cost-rate/share-cost-rate-dialog.html',
                    controller: 'ShareCostRateDialogController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    return $ocLazyLoad.load([
                                             'app/info/share-cost-rate/share-cost-rate-dialog.controller.js',
                                             'app/info/dept-type/dept-type.service.js'
                                             ]);
                }],
            	translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
	                $translatePartialLoader.addPart('shareCostRate');
	                $translatePartialLoader.addPart('global');
	                return $translate.refresh();
                }],
                entity: ['$stateParams', '$ocLazyLoad','$injector', function($stateParams, $ocLazyLoad,$injector) {
                	return $ocLazyLoad.load('app/info/share-cost-rate/share-cost-rate.service.js').then(
                			function(){
                				return $injector.get('ShareCostRate').get({id : $stateParams.id}).$promise;
                			}
                	);
                }],
                 previousState: ["$state", function ($state) {
 	                var currentStateData = {
 	                    name: $state.current.name || 'share-cost-rate',
 	                    params: $state.params,
 	                    url: $state.href($state.current.name, $state.params)
 	                };
 	                return currentStateData;
 	            }]
             }
        })
        .state('share-cost-rate.delete', {
            parent: 'share-cost-rate',
            url: '/delete/{id}',
            data: {
            	authorities: ['ROLE_INFO_BASIC']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/info/share-cost-rate/share-cost-rate-delete-dialog.html',
                    controller: 'ShareCostRateDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                    	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                            return $ocLazyLoad.load('app/info/share-cost-rate/share-cost-rate-delete-dialog.controller.js');
                        }],
                        entity: ['ShareCostRate', function(ShareCostRate) {
                            return ShareCostRate.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('share-cost-rate', null, { reload: 'share-cost-rate' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
