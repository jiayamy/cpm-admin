(function() {
    'use strict';

    angular
        .module('cpmApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('role-hardWorking', {
            parent: 'stat',
            url: '/role-hardWorking?originMonth&userId&userName',
            data: {
                authorities: ['ROLE_WORKHARDING'],
                pageTitle: 'cpmApp.roleHardWorking.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/stat/role-hardworking/role-hardworking.html',
                    controller: 'RoleHardWorkingController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'rhw.id,desc',
                    squash: true
                },
                originMonth: null,
                userId: null,
                userName:null
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    return $ocLazyLoad.load([
                                             'app/info/dept-info/dept-info.service.js',
                                             'app/stat/role-hardworking/role-hardworking.service.js',
                                             'app/stat/role-hardworking/role-hardworking.controller.js']);
                }],
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                    	page: PaginationUtil.parsePage($stateParams.page),
                    	sort: $stateParams.sort,
                    	predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        originMonth: $stateParams.originMonth,
                    	userId: $stateParams.userId,
                    	userName: $stateParams.userName
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                	$translatePartialLoader.addPart('roleHardWorking');
                	$translatePartialLoader.addPart('global');
                	$translatePartialLoader.addPart('deptInfo');
                    return $translate.refresh();
                }]
            }
        })
        
        .state('role-hardWorking.queryDept', {
            parent: 'role-hardWorking',
            url: '/queryDept?selectType&showChild',
            data: {
                authorities: ['ROLE_WORKHARDING']
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
                    		                         'app/info/dept-info/dept-info.service.js',
                                                     'app/info/dept-info/dept-info-query.controller.js'
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
        
        .state('role-hardWorking.chart', {
        	parent: 'role-hardWorking',
            url: '/chart/{id}/queryChart??beginningMonth',
            data: {
                authorities: ['ROLE_WORKHARDING']
            },
            views: {
                'content@': {
                    templateUrl: 'app/stat/role-hardworking/role-hardworking-chart.html',
                    controller: 'RoleHardWorkingChartController',
                    controllerAs: 'vm'
                }
            },
            params: {
                id : null
            },
            resolve: {
            	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                    return $ocLazyLoad.load([
                                             'app/stat/role-hardworking/role-hardworking-chart.service.js',
                                             'app/stat/role-hardworking/role-hardworking-chart.controller.js'
                                             ]);
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                	$translatePartialLoader.addPart('roleHardWorking');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
                pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                    return {
                    	beginningMonth: $stateParams.beginningMonth,
                        id : $stateParams.id
                    };
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: 'role-hardWorking',
                        params: {
                            page: {
                                value: '1',
                                squash: true
                            },
                            sort: {
                                value: 'rhw.id,asc',
                                squash: true
                            },
                            userId : null
                            
                        },
                        url: $state.href($state.current.name, {
                            page: {
                                value: '1',
                                squash: true
                            },
                            sort: {
                                value: 'rhw.id,asc',
                                squash: true
                            },
                            userId : null
                        })
                    };
                    return currentStateData;
                }]
                
            }
        })
    }

})();
