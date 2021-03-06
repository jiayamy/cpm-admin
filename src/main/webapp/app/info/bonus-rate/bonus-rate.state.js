(function() {
    'use strict';

    angular
        .module('cpmApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('bonus-rate', {
            parent: 'info',
            url: '/bonus-rate?page&sort&deptType&contractType',
            data: {
                authorities: ['ROLE_INFO_BASIC'],
                pageTitle: 'cpmApp.bonusRate.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/info/bonus-rate/bonus-rates.html',
                    controller: 'BonusRateController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'wbr.contractType,desc',
                    squash: true
                },
                deptType: null,
                contractType: null
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    return $ocLazyLoad.load(['app/info/dept-type/dept-type.service.js',
                                             'app/info/bonus-rate/bonus-rate.service.js',
                                             'app/info/bonus-rate/bonus-rate.controller.js'])
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
                    $translatePartialLoader.addPart('bonusRate');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('bonus-rate.new',{
        	parent: 'bonus-rate',
            url: '/new',
            data: {
            	 authorities: ['ROLE_INFO_BASIC'],
            },
            views:{
            	'content@':{
            		templateUrl: 'app/info/bonus-rate/bonus-rate-dialog.html',
                    controller: 'BonusRateDialogController',
                    controllerAs: 'vm'
            	}
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    return $ocLazyLoad.load('app/info/bonus-rate/bonus-rate-dialog.controller.js')
                }],
            	 translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                     $translatePartialLoader.addPart('bonusRate');
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
                        name: $state.current.name || 'bonus-rate',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('bonus-rate.edit', {
            parent: 'bonus-rate',
            url: '/edit/{id}',
            data: {
            	authorities: ['ROLE_INFO_BASIC']
            },
            views: {
            	'content@': {
                    templateUrl: 'app/info/bonus-rate/bonus-rate-dialog.html',
                    controller: 'BonusRateDialogController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
            		return $ocLazyLoad.load('app/info/bonus-rate/bonus-rate-dialog.controller.js')
                }],
            	translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
	                $translatePartialLoader.addPart('bonusRate');
	                $translatePartialLoader.addPart('global');
	                return $translate.refresh();
                }],
                entity: ['$stateParams', '$ocLazyLoad','$injector', function($stateParams, $ocLazyLoad,$injector) {
                	return $ocLazyLoad.load('app/info/bonus-rate/bonus-rate.service.js').then(
                			function(){
                				return $injector.get('BonusRate').get({id : $stateParams.id}).$promise;
                			}
                	);
                }],
                 previousState: ["$state", function ($state) {
 	                var currentStateData = {
 	                    name: $state.current.name || 'bonus-rate',
 	                    params: $state.params,
 	                    url: $state.href($state.current.name, $state.params)
 	                };
 	                return currentStateData;
 	            }]
             }
        })
        .state('bonus-rate.delete', {
            parent: 'bonus-rate',
            url: '/delete/{id}',
            data: {
            	authorities: ['ROLE_INFO_BASIC']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/info/bonus-rate/bonus-rate-delete-dialog.html',
                    controller: 'BonusRateDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                    	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    		return $ocLazyLoad.load('app/info/bonus-rate/bonus-rate-delete-dialog.controller.js')
                        }],
                        entity: ['BonusRate', function(BonusRate) {
                            return BonusRate.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('bonus-rate', null, { reload: 'bonus-rate' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
