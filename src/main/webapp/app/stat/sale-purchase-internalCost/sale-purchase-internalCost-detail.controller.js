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
//        vm.itemsPerPage = 4;
        vm.loadAll = loadAll;
        vm.back = back;
        
        vm.previousState = previousState.name;
        vm.userId = pagingParams.userId;
        
        vm.backParams = {};	//返回时所需参数
        vm.backParams.contractId = pagingParams.contractId;
        vm.backParams.userNameId = pagingParams.userNameId;
        vm.backParams.userName = pagingParams.userName;
        vm.backParams.statWeek = pagingParams.statWeek;
        vm.backParams.deptType = pagingParams.deptType;
        
        loadAll();

        function loadAll () {
        	SalePurchaseInternalCost.queryDetail({
                page: pagingParams.page - 1,
                size: vm.itemsPerPage,
                sort: sort(),
                userId : pagingParams.userId
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
                userId:vm.userId,
                contractId : pagingParams.contractId,
	            userNameId : pagingParams.userNameId,
	            userName : pagingParams.userName,
	            statWeek : pagingParams.statWeek,
	            deptType : pagingParams.deptType
            });
        }
        function back(){
        	$state.go('sale-purchase-internalCost',vm.backParams,null);
        }
//        function back(){
//        	$state.go('sale-purchase-internalCost',$rootScope.backDetail,null);
//        }
    }
})();
