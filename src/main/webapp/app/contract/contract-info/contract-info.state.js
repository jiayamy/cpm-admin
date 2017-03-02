(function() {
    'use strict';

    angular
        .module('cpmApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('contract-info', {
            parent: 'contract',
            url: '/contract-info?page&sort&serialNum&name&type&isPrepared&isEpibolic',
            data: {
                authorities: ['ROLE_CONTRACT_INFO'],
                pageTitle: 'cpmApp.contractInfo.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/contract/contract-info/contract-infos.html',
                    controller: 'ContractInfoController',
                    controllerAs: 'vm'
                }
            },
            params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'wci.id,desc',
                    squash: true
                },
                serialNum:null,
                name:null,
                type:null,
                isPrepared:null,
                isEpibolic:null
            },
            resolve: {
                pagingParams: ['$state','$stateParams', 'PaginationUtil', function ($state,$stateParams, PaginationUtil) {
                    return {
                        page: PaginationUtil.parsePage($stateParams.page),
                        sort: $stateParams.sort,
                        predicate: PaginationUtil.parsePredicate($stateParams.sort),
                        ascending: PaginationUtil.parseAscending($stateParams.sort),
                        serialNum: $stateParams.serialNum,
                        name: $stateParams.name,
                        type: $stateParams.type,
                        isPrepared: $stateParams.isPrepared,
                        isEpibolic: $stateParams.isEpibolic
                    };
                }],
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('contractInfo');
                    $translatePartialLoader.addPart('deptInfo');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('contract-info-detail', {
            parent: 'contract-info',
            url: '/detail/{id}',
            data: {
                authorities: ['ROLE_CONTRACT_INFO'],
                pageTitle: 'cpmApp.contractInfo.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/contract/contract-info/contract-info-detail.html',
                    controller: 'ContractInfoDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('contractInfo');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'ContractInfo', function($stateParams, ContractInfo) {
                    return ContractInfo.get({id : $stateParams.id}).$promise;
                }],
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                        name: $state.current.name || 'contract-info',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
       .state('contract-info-detail.edit',{
        	parent:'contract-info-detail',
        	url:'/edit',
        	data:{
        		authorities: ['ROLE_CONTRACT_INFO']
        	},
        	views:{
        		'content@':{
        			templateUrl: 'app/contract/contract-info/contract-info-dialog.html',
        			controller: 'ContractInfoDialogController',
        			controllerAs: 'vm'
        		}
        	},
        	resolve:{
        		 translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                     $translatePartialLoader.addPart('contractInfo');
                     $translatePartialLoader.addPart('deptInfo');
                     $translatePartialLoader.addPart('global');
                     return $translate.refresh();
                 }],
                 entity: ['$stateParams', 'ContractInfo', function($stateParams, ContractInfo) {
                     return ContractInfo.get({id : $stateParams.id}).$promise;
                 }],
                 previousState: ["$state", function ($state) {
                     var currentStateData = {
                         queryDept:'contract-info-detail.edit.queryDept',
                         name: $state.current.name || 'contract-info-detail',
                         params: $state.params,
                         url: $state.href($state.current.name, $state.params)
                     };
                     return currentStateData;
                 }]
        	}
        })
        .state('contract-info-detail.edit.queryDept', {
            parent: 'contract-info-detail.edit',
            url: '/queryDept?selectType&showChild&dataType',
            data: {
                authorities: ['ROLE_CONTRACT_INFO']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/info/dept-info/dept-info-query.html',
                    controller: 'DeptInfoQueryController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function() {
                            return {
                            	selectType : $stateParams.selectType,
                            	showChild : $stateParams.showChild,
                            	dataType:$stateParams.dataType
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
        .state('contract-info.new',{
        	parent: 'contract-info',
            url: '/new',
            data: {
                authorities: ['ROLE_CONTRACT_INFO']
            },
            views:{
            	'content@':{
            		templateUrl: 'app/contract/contract-info/contract-info-dialog.html',
                    controller: 'ContractInfoDialogController',
                    controllerAs: 'vm'
            	}
            },
            resolve: {
            	 translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                     $translatePartialLoader.addPart('contractInfo');
                     $translatePartialLoader.addPart('deptInfo');
                     $translatePartialLoader.addPart('global');
                     return $translate.refresh();
                 }],
                entity: function () {
                    return {
                        serialNum: null,
                        name: null,
                        amount: null,
                        type: null,
                        isPrepared: null,
                        isEpibolic: null,
                        startDay: null,
                        endDay: null,
                        taxRate: null,
                        taxes: null,
                        shareRate: null,
                        shareCost: null,
                        paymentWay: null,
                        contractor: null,
                        address: null,
                        postcode: null,
                        linkman: null,
                        contactDept: null,
                        telephone: null,
                        receiveTotal: null,
                        finishRate: null,
                        status: null,
                        creator: null,
                        createTime: null,
                        updator: null,
                        updateTime: null,
                        id: null
                    };
                },
                previousState: ["$state", function ($state) {
                    var currentStateData = {
                    	queryDept:'contract-info.new.queryDept',
                        name: $state.current.name || 'contract-info',
                        params: $state.params,
                        url: $state.href($state.current.name, $state.params)
                    };
                    return currentStateData;
                }]
            }
        })
        .state('contract-info.new.queryDept', {
            parent: 'contract-info.new',
            url: '/queryDept?selectType&showChild&dataType',
            data: {
                authorities: ['ROLE_CONTRACT_INFO']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/info/dept-info/dept-info-query.html',
                    controller: 'DeptInfoQueryController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function() {
                            return {
                            	selectType : $stateParams.selectType,
                            	showChild : $stateParams.showChild,
                            	dataType:$stateParams.dataType
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
        .state('contract-info.edit',{
        	parent:'contract-info',
        	url:'/edit/{id}',
        	data:{
        		authorities: ['ROLE_CONTRACT_INFO']
        	},
        	views:{
        		'content@':{
        			templateUrl: 'app/contract/contract-info/contract-info-dialog.html',
        			controller: 'ContractInfoDialogController',
        			controllerAs: 'vm',
        		}
        	},
        	resolve:{
        		 translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                     $translatePartialLoader.addPart('contractInfo');
                     $translatePartialLoader.addPart('deptInfo');
                     $translatePartialLoader.addPart('global');
                     return $translate.refresh();
                 }],
                 entity: ['$stateParams', 'ContractInfo', function($stateParams, ContractInfo) {
                     return ContractInfo.get({id : $stateParams.id}).$promise;
                 }],
                 previousState: ["$state", function ($state) {
                     var currentStateData = {
                     	queryDept:'contract-info.edit.queryDept',
                         name: $state.current.name || 'contract-info',
                         params: $state.params,
                         url: $state.href($state.current.name, $state.params)
                     };
                     return currentStateData;
                 }]
                 
        	}
        })
        .state('contract-info.finRate',{
        	parent:'contract-info',
        	url:'/finRate/{id}',
        	data:{
        		authorities: ['ROLE_CONTRACT_INFO']
        	},
        	views:{
        		'content@':{
        			templateUrl: 'app/contract/contract-info/contract-info-finRate.html',
        			controller: 'ContractInfofinRateController',
        			controllerAs: 'vm',
        		}
        	},
        	resolve:{
        		 translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                     $translatePartialLoader.addPart('contractInfo');
                     $translatePartialLoader.addPart('global');
                     return $translate.refresh();
                 }],
                 entity: ['$stateParams', 'ContractInfo', function($stateParams, ContractInfo) {
                     return ContractInfo.get({id : $stateParams.id}).$promise;
                 }]
        	}
        })
        .state('contract-info.edit.queryDept', {
            parent: 'contract-info.edit',
            url: '/queryDept?selectType&showChild&dataType',
            data: {
                authorities: ['ROLE_CONTRACT_INFO']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/info/dept-info/dept-info-query.html',
                    controller: 'DeptInfoQueryController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function() {
                            return {
                            	selectType : $stateParams.selectType,
                            	showChild : $stateParams.showChild,
                            	dataType:$stateParams.dataType
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
        
        .state('contract-info.delete', {
            parent: 'contract-info',
            url: '/delete/{id}',
            data: {
                authorities: ['ROLE_CONTRACT_INFO']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/contract/contract-info/contract-info-delete-dialog.html',
                    controller: 'ContractInfoDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['ContractInfo', function(ContractInfo) {
                            return ContractInfo.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('contract-info', null, { reload: 'contract-info' });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('contract-info.query', {
            parent: 'contract-info',
            url: '/queryDept?selectType&showChild',
            data: {
                authorities: ['ROLE_CONTRACT_INFO']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/info/dept-info/dept-info-query.html',
                    controller: 'DeptInfoQueryController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
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
        });
    }

})();
