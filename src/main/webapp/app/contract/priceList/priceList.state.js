(function() {
    'use strict';

    angular
        .module('cpmApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('priceList', {
            parent: 'contract',
            url: '/priceList?page&sort',
            data: {
                authorities: ['ROLE_ADMIN'],
                pageTitle: 'priceList.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/contract/priceList/priceList.html',
                    controller: 'PriceListController',
                    controllerAs: 'vm'
                }
            },    params: {
                page: {
                    value: '1',
                    squash: true
                },
                sort: {
                    value: 'id,asc',
                    squash: true
                }
            },
            resolve: {
            	 pagingParams: ['$stateParams', 'PaginationUtil', function ($stateParams, PaginationUtil) {
                     return {
                         page: PaginationUtil.parsePage($stateParams.page),
                         sort: $stateParams.sort,
                         predicate: PaginationUtil.parsePredicate($stateParams.sort),
                         ascending: PaginationUtil.parseAscending($stateParams.sort)
                     };
                 }],
                 translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                     $translatePartialLoader.addPart('priceList');
                     return $translate.refresh();
                 }]

             }        })
        .state('priceList-detail',{
        	parent: 'contract',
            url: '/priceList/:id',
            data: {
                authorities: ['ROLE_ADMIN'],
                pageTitle: 'priceList.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/contract/priceList/priceList-detail.html',
                    controller: 'PriceListDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('priceList');
                    return $translate.refresh();
                }]
            }
        })
        .state('priceList.edit', {
            parent: 'contract',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/contract/priceList/priceList-edit-dialog.html',
                    controller: 'PriceListEditContoller',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['PriceListService', function(PriceListService) {
                            return PriceListService.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('priceList', null, { reload: true });
                }, function() {
//                    $state.go('^');
                });
            }]
        })
        .state('priceList.new', {
            parent: 'contract',
            url: '/new',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/contract/priceList/priceList-edit-dialog.html',
                    controller: 'PriceListEditContoller',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                id: null, name: null, type: null, unitPrice: null, unit: null,
                                source: null, creator: null, creationTime: null, updater: null,
                                updateTime: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('priceList', null, { reload: true });
                }, function() {
                    $state.go('priceList');
                });
            }]
        })
        .state('priceList.delete', {
            parent: 'contract',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_ADMIN']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/contract/priceList/priceList-delete-dialog.html',
                    controller: 'PriceListDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['PriceListService', function(PriceListService) {
                            return PriceListService.get({id : $stateParams.id});
                        }]
                    }
                }).result.then(function() {
                    $state.go('priceList', null, { reload: true });
                }, function() {
//                    $state.go('^');
                });
            }]
        });
    }
})();
