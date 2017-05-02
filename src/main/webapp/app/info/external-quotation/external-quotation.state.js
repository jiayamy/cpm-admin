(function() {
    'use strict';

    angular
        .module('cpmApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('external-quotation', {
            parent: 'info',
            url: '/external-quotation?page&sort&grade',
            data: {
                authorities: ['ROLE_INFO_BASIC'],
                pageTitle: 'cpmApp.externalQuotation.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/info/external-quotation/external-quotations.html',
                    controller: 'ExternalQuotationController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'weq.grade,asc',
                    squash: true
                },
                grade: null
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    return $ocLazyLoad.load([
                                             'app/info/external-quotation/external-quotation.service.js',
                                             'app/info/external-quotation/external-quotation.controller.js']);
                }],
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        grade: $stateParams.grade
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('externalQuotation');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('external-quotation.new', {
            parent: 'external-quotation',
            url: '/new',
            data: {
                authorities: ['ROLE_INFO_BASIC'],
                pageTitle: 'cpmApp.externalQuotation.home.createOrEditLabel'
            },
            views: {
                'content@': {
                    templateUrl: 'app/info/external-quotation/external-quotation-dialog.html',
                    controller: 'ExternalQuotationDialogController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    return $ocLazyLoad.load('app/info/external-quotation/external-quotation-dialog.controller.js');
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                	$translatePartialLoader.addPart('externalQuotation');
                	$translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
                entity: function () {
                    return {
                        id: null,
                        grade: null,
                        externalQuotation: 0,
                        socialSecurityFund: 0,
                        otherExpense: 0,
                        costBasis: 0,
                        hourCost: 0
                    };
                },
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'external-quotation',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('external-quotation.edit', {
            parent: 'external-quotation',
            url: '/edit/{id}',
            data: {
                authorities: ['ROLE_INFO_BASIC'],
                pageTitle: 'cpmApp.externalQuotation.home.createOrEditLabel'
            },
            views: {
                'content@': {
                    templateUrl: 'app/info/external-quotation/external-quotation-dialog.html',
                    controller: 'ExternalQuotationDialogController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    return $ocLazyLoad.load('app/info/external-quotation/external-quotation-dialog.controller.js');
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                	$translatePartialLoader.addPart('externalQuotation');
                	$translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', '$ocLazyLoad','$injector', function($stateParams, $ocLazyLoad,$injector) {
                	return $ocLazyLoad.load('app/info/external-quotation/external-quotation.service.js').then(
                			function(){
                				return $injector.get('ExternalQuotation').get({id : $stateParams.id}).$promise;
                			}
                	);
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'external-quotation',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        });
    }

})();
