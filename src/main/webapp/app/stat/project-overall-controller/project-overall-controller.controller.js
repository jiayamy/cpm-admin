(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ProjectOverallController', ProjectOverallController);

    ProjectOverallController.$inject = ['$scope','$rootScope', '$state', 'ProjectOverall','ContractInfo', 'ParseLinks', 'AlertService', 'paginationConstants', 'pagingParams','DateUtils'];

    function ProjectOverallController ($scope,$rootScope, $state, ProjectOverall,ContractInfo, ParseLinks, AlertService, paginationConstants, pagingParams,DateUtils) {
    	var vm = this;
        vm.predicate = pagingParams.predicate;
        vm.reverse = pagingParams.ascending;
        vm.transition = transition;
        vm.itemsPerPage = paginationConstants.itemsPerPage;
        vm.clear = clear;
        vm.search = search;
        vm.searchQuery = {};
        var fromDate = pagingParams.fromDate;
        var toDate = pagingParams.toDate;
        if(fromDate && fromDate.length == 8){
        	fromDate = new Date(fromDate.substring(0,4),parseInt(fromDate.substring(4,6))-1,fromDate.substring(6,8));
        }
        if(toDate && toDate.length == 8){
        	toDate = new Date(toDate.substring(0,4),parseInt(toDate.substring(4,6))-1,toDate.substring(6,8));
        }
        
       
      //搜索中的参数
        vm.searchQuery.fromDate= fromDate;
        vm.searchQuery.toDate = toDate;
        vm.searchQuery.userId = pagingParams.userId;
        vm.searchQuery.contractId = pagingParams.contractId;
        
        if (!vm.searchQuery.fromDate && !vm.searchQuery.toDate && !vm.searchQuery.userId && !vm.searchQuery.contractId){
        	vm.haveSearch = null;
        }else{
        	vm.haveSearch = true;
        }
        
        vm.contractInfos = [];
        loadContractInfos();
        
        function loadContractInfos(){
        	ContractInfo.queryContractInfo(
        		{
        			
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
        		}
        	);
        }
        
        //加载列表信息
        loadAll();
        function loadAll () {
        	if (pagingParams.fromDate == undefined) {
        		pagingParams.fromDate = "";
			}
        	if (pagingParams.toDate == undefined) {
				pagingParams.toDate = "";
			}
        	if (pagingParams.userId == undefined) {
				pagingParams.userId = "";
			}
        	if(pagingParams.contractId == undefined){
        		pagingParams.contractId = "";
        	}
        	ProjectOverall.query({
        		fromDate: pagingParams.fromDate,
        		toDate: pagingParams.toDate,
        		contractId: pagingParams.contractId,
        		userId: pagingParams.userId,
                page: pagingParams.page - 1,
                size: vm.itemsPerPage,
                sort: sort()
            }, onSuccess, onError);
        	
        	function sort() {
                var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
                if (vm.predicate !== 'wpo.id') {
                    result.push('wpo.id');
                }
                return result;
            }
        	function onSuccess(data, headers) {
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
                vm.queryCount = vm.totalItems;
                vm.projectOverAlls = handleData(data);
                vm.page = pagingParams.page;
            }
        	function onError(error) {
                AlertService.error(error.data.message);
            }
        }
        
        function handleData(data){
        	if (data.length > 0) {
				for(var i = 0; i< data.length ; i++){
					if (data[i].salesman == "") {
						data[i].salesman = data[i].consultants;
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
                fromDate: DateUtils.convertLocalDateToFormat(vm.searchQuery.fromDate,"yyyyMMdd"),
                toDate: DateUtils.convertLocalDateToFormat(vm.searchQuery.toDate,"yyyyMMdd"),
                userId:vm.searchQuery.userId,
                contractId: vm.searchQuery.contractId ? vm.searchQuery.contractId.key: ""
            });
        }
        
        function search() {
            if (!vm.searchQuery.fromDate && !vm.searchQuery.toDate && !vm.searchQuery.userId && !vm.searchQuery.contractId){
                return vm.clear();
            }
            vm.links = null;
            vm.page = 1;
            vm.predicate = 'wpo.id';
            vm.reverse = false;
            vm.haveSearch = true;
            vm.transition();
        }
        
        function clear() {
            vm.links = null;
            vm.page = 1;
            vm.predicate = 'wpo.id';
            vm.reverse = true;
            vm.searchQuery = {};
            vm.haveSearch = null;
            vm.transition();
        }
        
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.datePickerOpenStatus.fromDate = false;
        vm.datePickerOpenStatus.toDate = false;
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
