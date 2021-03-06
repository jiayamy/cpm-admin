(function() {
    'use strict';

    angular
        .module('cpmApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('consultant-bonus', {
            parent: 'stat',
            url: '/consultant-bonus?page&contractId&consultantsId&statWeek&consultantsName',
            data: {
                authorities: ['ROLE_STAT_CONSULTANT_BONUS'],
                pageTitle: 'cpmApp.consultantBonus.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/stat/consultant-bonus/consultant-bonus.html',
                    controller: 'ConsultantBonusController',
                    controllerAs: 'vm'
                }
            },
            params: {
            	page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'm.id,desc',
                    squash: true
                },
                contractId : null,
                consultantsId : null,
                statWeek : null,
                consultantsName: null
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    return $ocLazyLoad.load([
                                             'app/stat/consultant-bonus/consultant-bonus.service.js',
                                             'app/stat/consultant-bonus/consultant-bonus.controller.js',
                                             'app/contract/contract-info/contract-info.service.js']);
                }],
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                    	page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        contractId: $stateParams.contractId,
                        consultantsName: $stateParams.consultantsName,
                        consultantsId: $stateParams.consultantsId,
                        statWeek : $stateParams.statWeek
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('consultantBonus');
                    $translatePartialLoader.addPart('deptInfo');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('consultant-bonus.queryDept', {
            parent: 'consultant-bonus',
            url: '/queryDept?selectType&showChild',
            data: {
                authorities: ['ROLE_STAT_CONSULTANT_BONUS']
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
        .state('consultant-bonus-contractRecord',{
        	parent: 'consultant-bonus',
        	url: '/contractRecord/{contId}',
        	data:{
        		authorities: ['ROLE_STAT_CONSULTANT_BONUS'],
        		pageTitle: 'cpmApp.consultantBonus.contractRecord.title'
        	},
        	views:{
        		'content@':{
        			templateUrl: 'app/stat/consultant-bonus/consultant-bonus-contractRecord.html',
                    controller: 'ConsultantBonusContractRecordController',
                    controllerAs: 'vm'
        		}
        	},
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    return $ocLazyLoad.load('app/stat/consultant-bonus/consultant-bonus-contractRecord.controller.js');
                }],
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        contId: $stateParams.contId,
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('consultantBonus');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'consultant-bonus',
                        params: $state.params,
                        url: $state.href($state.current.name , $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
    }
})();
