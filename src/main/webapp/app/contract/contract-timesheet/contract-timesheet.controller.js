(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ContractTimesheetController', ContractTimesheetController);

    ContractTimesheetController.$inject = ['$scope','$rootScope', '$state', 'ContractTimesheet', 'ContractInfo', 'ParseLinks', 'AlertService', 'paginationConstants', 'pagingParams','DateUtils'];

    function ContractTimesheetController ($scope,$rootScope, $state, ContractTimesheet, ContractInfo, ParseLinks, AlertService, paginationConstants, pagingParams, DateUtils) {
        var vm = this;

        vm.loadPage = loadPage;
        vm.predicate = pagingParams.predicate;
        vm.reverse = pagingParams.ascending;
        vm.transition = transition;
        vm.itemsPerPage = paginationConstants.itemsPerPage;
        
        vm.clear = clear;
        vm.search = search;
        vm.loadAll = loadAll;
        
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        
        vm.searchQuery = {};
        vm.searchQuery.workDay= DateUtils.convertDayToDate(pagingParams.workDay);
        vm.searchQuery.contractId = pagingParams.contractId;	//需要自定义
        vm.searchQuery.userId = pagingParams.userId;
        vm.searchQuery.userName = pagingParams.userName;
        if (!vm.searchQuery.workDay && !vm.searchQuery.contractId && !vm.searchQuery.userId){
        	vm.haveSearch = null;
        }else{
        	vm.haveSearch = true;
        }
        //加载合同
        loadContractInfos();
        function loadContractInfos(){
        	ContractInfo.queryContractInfo({
        		
        	},
        	function(data, headers){
    			vm.contractInfos = data;
        		if(vm.contractInfos && vm.contractInfos.length > 0){
        			for(var i = 0; i < vm.contractInfos.length; i++){
        				if(pagingParams.contractId == vm.contractInfos[i].key){
        					vm.searchQuery.contractId = vm.contractInfos[i];
        				}
        			}
        		}
    		},
    		function(error){
    			AlertService.error(error.data.message);
    		});
        }
        //加载列表
        loadAll();
        function loadAll () {
            ContractTimesheet.query({
            	workDay: pagingParams.workDay,
            	contractId: pagingParams.contractId,
            	userId: pagingParams.userId,
                page: pagingParams.page - 1,
                size: vm.itemsPerPage,
                sort: sort()
            }, onSuccess, onError);
            function sort() {
                var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
                if (vm.predicate !== 'wut.id') {
                    result.push('wut.id');
                }
                return result;
            }
            function onSuccess(data, headers) {
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
                vm.queryCount = vm.totalItems;
                vm.contractTimesheets = handleData(data);
                vm.page = pagingParams.page;
            }
            function onError(error) {
                AlertService.error(error.data.message);
            }
        }
        function handleData(data){
        	if(data.length > 0){
        		for(var i = 0; i< data.length ; i++){
        			if(data[i].status == 1){
        				data[i].status = "正常";
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
            	workDay: DateUtils.convertLocalDateToFormat(vm.searchQuery.workDay,"yyyyMMdd"),
            	contractId: vm.searchQuery.contractId ? vm.searchQuery.contractId.key : "",
            	userId: vm.searchQuery.userId,
            	userName: vm.searchQuery.userName,
                page: vm.page,
                sort: vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')
            });
        }

        function search() {
        	if (!vm.searchQuery.workDay && !vm.searchQuery.contractId && !vm.searchQuery.userId){
                return vm.clear();
            }
            vm.links = null;
            vm.page = 1;
            vm.predicate = 'wut.id';
            vm.reverse = false;
            vm.transition();
        }

        function clear() {
            vm.links = null;
            vm.page = 1;
            vm.predicate = 'wut.id';
            vm.reverse = false;
            vm.searchQuery = {};
            vm.transition();
        }
        
        vm.datePickerOpenStatus.workDay = false;
        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
        
        var unsubscribe = $rootScope.$on('cpmApp:deptInfoSelected', function(event, result) {
        	vm.searchQuery.userId = result.objId;
        	vm.searchQuery.userName = result.name;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
