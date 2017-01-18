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
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('holidayInfo');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'HolidayInfo', function($stateParams, HolidayInfo) {
                    return HolidayInfo.get({id : $stateParams.id}).$promise;
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
        .state('holiday-info-detail.edit', {
            parent: 'holiday-info-detail',
            url: '/edit',
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
	            translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
	                $translatePartialLoader.addPart('holidayInfo');
	                return $translate.refresh();
	            }],
	            entity: ['$stateParams', 'HolidayInfo', function($stateParams, HolidayInfo) {
	                return HolidayInfo.get({id : $stateParams.id}).$promise;
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
            	translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('holidayInfo');
                    return $translate.refresh();
                }],
                entity: function () {
                    return {
                        currDay: null,
                        type: null,
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
	            translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
	                $translatePartialLoader.addPart('holidayInfo');
	                return $translate.refresh();
	            }],
	            entity: ['$stateParams', 'HolidayInfo', function($stateParams, HolidayInfo) {
	                return HolidayInfo.get({id : $stateParams.id}).$promise;
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
        .state('holiday-info.delete', {
            parent: 'holiday-info',
            url: '/delete/{id}',
            data: {
                authorities: ['ROLE_INFO_BASIC']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/info/holiday-info/holiday-info-delete-dialog.html',
                    controller: 'HolidayInfoDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['HolidayInfo', function(HolidayInfo) {
                            return HolidayInfo.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('holiday-info', null, { reload: 'holiday-info' });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
