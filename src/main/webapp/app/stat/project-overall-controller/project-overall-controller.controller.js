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
        
        vm.exportXls = exportXls;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        var today = new Date();
        
        if (pagingParams.toDate == undefined) {
			pagingParams.toDate = DateUtils.convertLocalDateToFormat(today,"yyyyMMdd");;
		}
      //搜索中的参数
        vm.searchQuery.fromDate = DateUtils.convertDayToDate(pagingParams.fromDate);
        vm.searchQuery.toDate = DateUtils.convertDayToDate(pagingParams.toDate);
        vm.searchQuery.userId = pagingParams.userId;
        vm.searchQuery.contractId = pagingParams.contractId;
        vm.searchQuery.userName = pagingParams.userName;
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
                userName: vm.searchQuery.userName,
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
            vm.searchQuery.toDate = new Date();
            vm.haveSearch = true;
            vm.transition();
        }
        function exportXls(){//导出Xls
        	var url = "api/project-overall/exportXls";
        	var c = 0;
        	var fromDate = DateUtils.convertLocalDateToFormat(vm.searchQuery.fromDate,"yyyyMMdd");
        	var toDate = DateUtils.convertLocalDateToFormat(vm.searchQuery.toDate,"yyyyMMdd");
        	if (pagingParams.fromDate == undefined) {
        		pagingParams.fromDate = "";
			}
        	var contractId = vm.searchQuery.contractId && vm.searchQuery.contractId.key? vm.searchQuery.contractId.key : vm.searchQuery.contractId;
    		var userId = vm.searchQuery.userId;
    		if(toDate){
    			if(c == 0){
    				c++;
    				url += "?";
    			}else{
    				url += "&";
    			}
    			url += "toDate="+encodeURI(toDate);
    		}
    		if(fromDate){
    			if(c == 0){
    				c++;
    				url += "?";
    			}else{
    				url += "&";
    			}
    			url += "fromDate="+encodeURI(fromDate);
    		}
    		if(contractId){
    			if(c == 0){
    				c++;
    				url += "?";
    			}else{
    				url += "&";
    			}
    			url += "contractId="+encodeURI(contractId);
    		}
    		if(userId){
    			if(c == 0){
    				c++;
    				url += "?";
    			}else{
    				url += "&";
    			}
    			url += "userId="+encodeURI(userId);
    		}
    		
        	window.open(url);
        }
        
        
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
