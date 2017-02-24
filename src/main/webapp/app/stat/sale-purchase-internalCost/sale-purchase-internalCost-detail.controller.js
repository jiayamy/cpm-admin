(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('SalePurchaseInternalCostDetailController', SalePurchaseInternalCostDetailController);

    SalePurchaseInternalCostDetailController.$inject = ['$rootScope', '$scope', '$state', 'DateUtils','SalePurchaseInternalCost','paginationConstants','ParseLinks', 'AlertService', 'pagingParams','previousState'];

    function SalePurchaseInternalCostDetailController ($rootScope,$scope, $state,DateUtils, SalePurchaseInternalCost,paginationConstants, ParseLinks, AlertService, pagingParams,previousState) {
        var vm = this;

        vm.loadPage = loadPage;
        vm.predicate = pagingParams.predicate;
        vm.reverse = pagingParams.ascending;
        vm.transition = transition;
        vm.itemsPerPage = paginationConstants.itemsPerPage;
//        vm.itemsPerPage = 3;
        vm.loadAll = loadAll;
        
        vm.previousState = previousState.name;
        vm.contId = pagingParams.contId;
        
        loadAll();

        function loadAll () {
        	SalePurchaseInternalCost.queryDetail({
                page: pagingParams.page - 1,
                size: vm.itemsPerPage,
                sort: sort(),
                contId : pagingParams.contId
            }, onSuccess, onError);
           
            function sort() {
                var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
                if (vm.predicate !== 'p.id') {
                    result.push('p.id');
                }
                return result;
            }
            function onSuccess(data, headers) {
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
                vm.queryCount = vm.totalItems;
                vm.salePurchaseInternalCosts = data;
                vm.page = pagingParams.page;
            }
            function onError(error) {
                AlertService.error(error.data.message);
            }
        }

        function loadPage(page) {
            vm.page = page;
            vm.transition();
        }

        function transition() {
            $state.transitionTo($state.$current, {
                page: vm.page,
                sort: vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc'),
                contractId:vm.contId
            });
        }
    }
})();
