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
            url: '/consultant-bonus?page&contractId&fromDate&toDate',
            data: {
                authorities: ['ROLE_STAT_PROJECT'],
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
                fromDate : null,
                toDate : null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        contractId: $stateParams.contractId,
                        fromDate : $stateParams.fromDate,
                        toDate : $stateParams.toDate
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('consultantBonus');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('consultant-bonus-detail',{
        	parent: 'consultant-bonus',
        	url: '/detail/{id}',
        	data:{
        		authorities: ['ROLE_STAT_PROJECT'],
        		pageTitle: 'cpmApp.consultantBonus.detail.title'
        	},
        	views:{
        		'content@':{
        			templateUrl: 'app/stat/consultant-bonus/consultant-bonus-detail.html',
                    controller: 'ConsultantBonusDetailController',
                    controllerAs: 'vm'
        		}
        	},
        	resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('consultantBonus');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'ConsultantBonus', function($stateParams, ConsultantBonus) {
                    return ConsultantBonus.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'consultant-bonus',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('consultant-bonus-contractRecord',{
        	parent: 'consultant-bonus',
        	url: '/contractRecord?page&contId',
        	data:{
        		authorities: ['ROLE_STAT_PROJECT'],
        		pageTitle: 'cpmApp.consultantBonus.contractRecord.title'
        	},
        	views:{
        		'content@':{
        			templateUrl: 'app/stat/consultant-bonus/consultant-bonus-contractRecord.html',
                    controller: 'ConsultantBonusContractRecordController',
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
                }
//                ,
//                contractId : null,
//                fromDate : null,
//                toDate : null
            },
            resolve: {
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        contId: $stateParams.contId
//                        ,
//                        fromDate : $stateParams.fromDate,
//                        toDate : $stateParams.toDate
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
