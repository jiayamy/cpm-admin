(function() {
    'use strict';

    angular
        .module('cpmApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('holiday-info', {
            parent: 'info',
            url: '/holiday-info?page&sort&fromCurrDay&toCurrDay',
            data: {
                authorities: ['ROLE_INFO_BASIC'],
                pageTitle: 'cpmApp.holidayInfo.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/info/holiday-info/holiday-infos.html',
                    controller: 'HolidayInfoController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'currDay,desc',
                    squash: true
                },
                fromCurrDay: null,
                toCurrDay: null
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    return $ocLazyLoad.load([
                                             'app/info/holiday-info/holiday-info.service.js',
                                             'app/info/holiday-info/holiday-info.controller.js']);
                }],
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        fromCurrDay:$stateParams.fromCurrDay,
                        toCurrDay:$stateParams.toCurrDay
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('holidayInfo');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('holiday-info-detail', {
            parent: 'holiday-info',
            url: '/detail/{id}',
            data: {
                authorities: ['ROLE_INFO_BASIC'],
                pageTitle: 'cpmApp.holidayInfo.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/info/holiday-info/holiday-info-detail.html',
                    controller: 'HolidayInfoDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    return $ocLazyLoad.load('app/info/holiday-info/holiday-info-detail.controller.js');
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('holidayInfo');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', '$ocLazyLoad','$injector', function($stateParams, $ocLazyLoad, $injector) {
                	return $ocLazyLoad.load('app/info/holiday-info/holiday-info.service.js').then(
                			function(){
                				return $injector.get('HolidayInfo').get({id : $stateParams.id}).$promise;
                			}
                	);
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'holiday-info',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('holiday-info.new', {
            parent: 'holiday-info',
            url: '/new',
            pageTitle: 'cpmApp.holidayInfo.home.createOrEditLabel',
            data: {
                authorities: ['ROLE_INFO_BASIC']
            },
            views: {
                'content@': {
                    templateUrl: 'app/info/holiday-info/holiday-info-dialog.html',
                    controller: 'HolidayInfoDialogController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    return $ocLazyLoad.load('app/info/holiday-info/holiday-info-dialog.controller.js');
                }],
            	translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('holidayInfo');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
                entity: function () {
                    return {
                        currDay: null,
                        type: 4,
                        creator: null,
                        createTime: null,
                        updator: null,
                        updateTime: null,
                        id: null
                    };
                }
            }
        })
        .state('holiday-info.edit', {
            parent: 'holiday-info',
            url: '/edit/{id}',
            data: {
                authorities: ['ROLE_INFO_BASIC']
            },
            views: {
                'content@': {
                    templateUrl: 'app/info/holiday-info/holiday-info-dialog.html',
                    controller: 'HolidayInfoDialogController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    return $ocLazyLoad.load('app/info/holiday-info/holiday-info-dialog.controller.js');
                }],
	            translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
	                $translatePartialLoader.addPart('holidayInfo');
	                $translatePartialLoader.addPart('global');
	                return $translate.refresh();
	            }],
	            entity: ['$stateParams', '$ocLazyLoad','$injector', function($stateParams, $ocLazyLoad, $injector) {
                	return $ocLazyLoad.load('app/info/holiday-info/holiday-info.service.js').then(
                			function(){
                				return $injector.get('HolidayInfo').get({id : $stateParams.id}).$promise;
                			}
                	);
                }],
	            previousState: ["$state", function ($state) {
	                var currentStateData = {
	                    name: $state.current.name || 'holiday-info-detail',
	                    params: $state.params,
	                    url: $state.href($state.current.name, $state.params)
	                };
	                return currentStateData;
	            }]
            }
        })
        .state('holiday-info.editByCalendar', {
            parent: 'holiday-info',
            url: '/calendar/{id}',
            data: {
                authorities: ['ROLE_INFO_BASIC']
            },
            views: {
                'content@': {
                    templateUrl: 'app/info/holiday-info/holiday-info-calendar.html',
                    controller: 'HolidayInfoCalendarController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    return $ocLazyLoad.load('app/info/holiday-info/holiday-info-calendar.controller.js');
                }],
	            translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
	                $translatePartialLoader.addPart('holidayInfo');
	                $translatePartialLoader.addPart('global');
	                return $translate.refresh();
	            }],
	            entity: ['$stateParams', '$ocLazyLoad','$injector', function($stateParams, $ocLazyLoad, $injector) {
                	return $ocLazyLoad.load('app/info/holiday-info/holiday-info.service.js').then(
                			function(){
                				return $injector.get('HolidayInfo').get({id : $stateParams.id}).$promise;
                			}
                	);
                }],
	            previousState: ["$state", function ($state) {
	                var currentStateData = {
	                    name: $state.current.name || 'holiday-info',
	                    params: $state.params,
	                    url: $state.href($state.current.name, $state.params)
	                };
	                return currentStateData;
	            }]
            }
        })
    }

})();
