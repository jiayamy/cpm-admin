(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('UserCostController', UserCostController);

    UserCostController.$inject = ['$scope', '$state', 'UserCost', 'ParseLinks', 'AlertService', 'paginationConstants', 'pagingParams','DateUtils'];

    function UserCostController ($scope, $state, UserCost, ParseLinks, AlertService, paginationConstants, pagingParams,DateUtils) {
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
        vm.searchQuery.serialNum = pagingParams.serialNum;
        vm.searchQuery.userName = pagingParams.userName;
        vm.sdf = pagingParams.costMonth+"";
        vm.searchQuery.costMonth = pagingParams.costMonth?new Date(vm.sdf.substring(0,4),vm.sdf.substring(4,6)-1):"";
        
        vm.statuss = [{key:1,val:'可用'},{key:2,val:'删除'}];
        
        for(var i = 0; i < vm.statuss.length; i++){
        	if(pagingParams.status == vm.statuss[i].key){
        		vm.searchQuery.status = vm.statuss[i];
        	}
        }
        if (!vm.searchQuery.serialNum && !vm.searchQuery.userName && !vm.searchQuery.costMonth &&!vm.searchQuery.status){
        	vm.haveSearch = null;
        }else{
        	vm.haveSearch = true;
        }

        loadAll();

        function loadAll () {
            UserCost.query({
                page: pagingParams.page - 1,
                size: vm.itemsPerPage,
                sort: sort(),
                serialNum:pagingParams.serialNum,
                userName:pagingParams.userName,
                costMonth:pagingParams.costMonth,
                status:pagingParams.status
            }, onSuccess, onError);
            function sort() {
                var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
                if (vm.predicate !== 'wuc.id') {
                    result.push('wuc.id');
                }
                return result;
            }
            function onSuccess(data, headers) {
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
                vm.queryCount = vm.totalItems;
                vm.userCosts = handleData(data);
                vm.page = pagingParams.page;
            }
            function onError(error) {
                AlertService.error(error.data.message);
            }
        }
        
        function handleData(data){
        	if(data.length>0){
        		for(var i=0;i<data.length;i++){
        			if(data[i].status == 1){
        				data[i].status = "可用";
        			}else if(data[i].status == 2){
        				data[i].status = "删除";
        			}
        		}
        	}
        	return data;
        }

        function loadPage(page) {
            vm.page = page;
            vm.transition();
        }

        function transition() {
            $state.transitionTo($state.$current, {
                page: vm.page,
                sort: vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc'),
                serialNum:vm.searchQuery.serialNum,
                userName:vm.searchQuery.userName,
                costMonth:DateUtils.convertLocalDateToFormat(vm.searchQuery.costMonth,"yyyyMM"),
                status:vm.searchQuery.status?vm.searchQuery.status.key:"",
            });
        }

        function search(searchQuery) {
            if (!searchQuery){
                return vm.clear();
            }
            vm.links = null;
            vm.page = 1;
            vm.predicate = 'wuc.id';
            vm.reverse = false;
            vm.haveSearch = true;
            vm.transition();
        }

        function clear() {
            vm.links = null;
            vm.page = 1;
            vm.predicate = 'wuc.id';
            vm.reverse = false;
            vm.haveSearch = false;
            vm.searchQuery = {};
            vm.transition();
        }
        
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.datePickerOpenStatus.costMonth = false;
        function openCalendar(data){
        	vm.datePickerOpenStatus[data] = true;
        }
    }
})();
