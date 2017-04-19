(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('SaleWeeklyStatController', SaleWeeklyStatController);

    SaleWeeklyStatController.$inject = ['$rootScope', '$scope', '$state', 'DateUtils','SaleWeeklyStat','DeptInfo', 'ParseLinks', 'AlertService', 'paginationConstants', 'pagingParams'];

    function SaleWeeklyStatController ($rootScope, $scope, $state,DateUtils, SaleWeeklyStat, DeptInfo, ParseLinks, AlertService, paginationConstants, pagingParams) {
        var vm = this;

        vm.loadPage = loadPage;
        vm.predicate = pagingParams.predicate;
        vm.reverse = pagingParams.ascending;
        vm.transition = transition;
        vm.itemsPerPage = paginationConstants.itemsPerPage;
        vm.clear = clear;
        vm.search = search;
        vm.loadAll = loadAll;
        vm.loadSaleDept = loadSaleDept;
        vm.searchQuery = {};
        vm.searchQuery.deptId = pagingParams.deptId;
        vm.searchQuery.deptName = pagingParams.deptName;
        if (!vm.searchQuery.deptId){
        	vm.haveSearch = null;
        }else{
        	vm.haveSearch = true;
        }
        loadSaleDept();
        function loadSaleDept(){
        	DeptInfo.getPrimarySaleDepts({
        		
        	},
        	function(data, headers){
        		vm.saleDeptInfos = data;
        		if(vm.saleDeptInfos && vm.saleDeptInfos.length > 0){
        			for(var i = 0; i < vm.saleDeptInfos.length; i++){
        				if(pagingParams.deptId == vm.saleDeptInfos[i].id){
        					vm.searchQuery.deptId = vm.saleDeptInfos[i];
        				}
        			}
        		}
        	},
        	function(error){
        		AlertService.error(error.data.message);
        	});
        }
        
        loadAll();

        function loadAll () {
        	if(pagingParams.deptId == undefined){
        		pagingParams.deptId = "";
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
                deptId:vm.searchQuery.deptId?vm.searchQuery.deptId.id:""
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
            vm.reverse = false;
            vm.searchQuery = {};
            vm.haveSearch = null;
            vm.transition();
        }
    }
})();
