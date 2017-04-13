(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('SaleWeeklyStatController', SaleWeeklyStatController);

    SaleWeeklyStatController.$inject = ['$rootScope', '$scope', '$state', 'DateUtils','SaleWeeklyStat', 'ParseLinks', 'AlertService', 'paginationConstants', 'pagingParams'];

    function SaleWeeklyStatController ($rootScope, $scope, $state,DateUtils, SaleWeeklyStat, ParseLinks, AlertService, paginationConstants, pagingParams) {
        var vm = this;

        vm.loadPage = loadPage;
        vm.predicate = pagingParams.predicate;
        vm.reverse = pagingParams.ascending;
        vm.transition = transition;
        vm.itemsPerPage = paginationConstants.itemsPerPage;
        vm.clear = clear;
        vm.search = search;
        vm.loadAll = loadAll;
        vm.searchQuery = {};
        vm.searchQuery.deptId = pagingParams.deptId;
        vm.searchQuery.deptName = pagingParams.deptName;
//        vm.searchQuery.contractId= pagingParams.contractId;
//        vm.contractInfos = [];
        if (!vm.searchQuery.deptId){
        	vm.haveSearch = null;
        }else{
        	vm.haveSearch = true;
        }
//        loadContract();
//        function loadContract(){
//        	ContractInfo.queryContractInfo({
//        		
//        	},
//        	function(data, headers){
//        		vm.contractInfos = data;
//        		if(vm.contractInfos && vm.contractInfos.length > 0){
//        			for(var i = 0; i < vm.contractInfos.length; i++){
//        				if(pagingParams.contractId == vm.contractInfos[i].key){
//        					vm.searchQuery.contractId = vm.contractInfos[i];
//        				}
//        			}
//        		}
//        	},
//        	function(error){
//        		AlertService.error(error.data.message);
//        	});
//        }
        
        loadAll();

        function loadAll () {
        	if(pagingParams.contractId == undefined){
        		pagingParams.contractId = "";
        	}
        	SaleWeeklyStat.query({
                page: pagingParams.page - 1,
                size: vm.itemsPerPage,
                sort: sort(),
                deptId : pagingParams.deptId
            }, onSuccess, onError);
           
            function sort() {
                var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
                if (vm.predicate !== 's.id') {
                    result.push('s.id');
                }
                return result;
            }
            function onSuccess(data, headers) {
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
                vm.queryCount = vm.totalItems;
                vm.saleWeeklyStats = data;
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
                deptId:vm.searchQuery.deptId,
                deptName:vm.searchQuery.deptName
            });
        }

        function search(searchQuery) {
        	if (!vm.searchQuery.deptId){
                return vm.clear();
            }
            vm.links = null;
            vm.page = 1;
            vm.predicate = 's.id';
            vm.reverse = false;
            vm.haveSearch = true;
            vm.transition();
        }

        function clear() {
            vm.links = null;
            vm.page = 1;
            vm.predicate = 's.id';
            vm.reverse = true;
            vm.searchQuery = {};
            vm.haveSearch = null;
            vm.transition();
        }
        
        var unsubscribe = $rootScope.$on('cpmApp:deptInfoSelected', function(event, result) {
        	vm.searchQuery.deptId = result.objId;
        	vm.searchQuery.deptName = result.name;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
