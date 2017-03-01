(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ProjectSupportBonusController', ProjectSupportBonusController);

    ProjectSupportBonusController.$inject = ['$scope','$rootScope', '$state', 'ProjectSupportBonus','ProjectInfo','DeptType', 'ParseLinks', 'AlertService', 'paginationConstants', 'pagingParams','DateUtils'];

    function ProjectSupportBonusController ($scope,$rootScope, $state, ProjectSupportBonus,ProjectInfo,DeptType, ParseLinks, AlertService, paginationConstants, pagingParams,DateUtils) {
    	var vm = this;
        vm.itemsPerPage = paginationConstants.itemsPerPage;
        vm.transition = transition;
        vm.clear = clear;
        vm.search = search;
        vm.searchQuery = {};
        vm.exportXls = exportXls;
        
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        var today = new Date();
        
        if (pagingParams.statWeek == undefined) {
			pagingParams.statWeek = DateUtils.convertLocalDateToFormat(today,"yyyyMMdd");;
		}
      //搜索中的参数
        vm.searchQuery.statWeek = DateUtils.convertDayToDate(pagingParams.statWeek);
        vm.searchQuery.contractId = pagingParams.contractId;
        vm.searchQuery.deptType = pagingParams.deptType;
        if (!vm.searchQuery.statWeek && !vm.searchQuery.contractId && !vm.searchQuery.deptType){
        	vm.haveSearch = null;
        }else{
        	vm.haveSearch = true;
        }
        
        vm.contractInfos = [];
        loadContractInfos();
        
        function loadContractInfos(){
        	ProjectInfo.queryUserContract(
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
        
      //部门类型
        loadDeptType();
        function loadDeptType(){
        	DeptType.getAllForCombox(
    			{
        		},
        		function(data, headers){
        			vm.deptTypes = data;
            		if(vm.deptTypes && vm.deptTypes.length > 0){
            			for(var i = 0; i < vm.deptTypes.length; i++){
            				if(pagingParams.deptType == vm.deptTypes[i].key){
            					vm.searchQuery.deptType = vm.deptTypes[i];
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
        	ProjectSupportBonus.query({
        		statWeek: pagingParams.statWeek,
        		contractId: pagingParams.contractId,
        		deptType: pagingParams.deptType,
        		page: pagingParams.page - 1,
                size: vm.itemsPerPage
            }, onSuccess, onError);
        	function onSuccess(data, headers) {
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
                vm.queryCount = vm.totalItems;
                vm.projectSupportBonuses = handleData(data);
                vm.page = pagingParams.page;
            }
        	function onError(error) {
                AlertService.error(error.data.message);
            }
        }
        
        function handleData(data){
        	return data;
        }
        
        function loadPage(page) {
            vm.page = page;
            vm.transition();
        }
        
        function transition() {
            $state.transitionTo($state.$current, {
                page: vm.page,
                statWeek: DateUtils.convertLocalDateToFormat(vm.searchQuery.statWeek,"yyyyMMdd"),
                contractId: vm.searchQuery.contractId ? vm.searchQuery.contractId.key: "",
                deptType: vm.searchQuery.deptType ? vm.searchQuery.deptType.key : "",
            });
        }
        
        function search() {
            if (!vm.searchQuery.statWeek && !vm.searchQuery.contractId && !vm.searchQuery.deptId){
                return vm.clear();
            }
            vm.links = null;
            vm.page = 1;
            vm.haveSearch = true;
            vm.transition();
        }
        
        function clear() {
            vm.links = null;
            vm.page = 1;
            vm.searchQuery = {};
            vm.searchQuery.statWeek = new Date();
            vm.haveSearch = true;
            vm.transition();
        }
        function exportXls(){//导出Xls
        	var url = "api/project-support-bonus/exportXls";
        	var c = 0;
        	var statWeek = DateUtils.convertLocalDateToFormat(vm.searchQuery.statWeek,"yyyyMMdd");
        	var contractId = vm.searchQuery.contractId && vm.searchQuery.contractId.key? vm.searchQuery.contractId.key : vm.searchQuery.contractId;
        	var deptType = vm.searchQuery.deptType && vm.searchQuery.deptType.key? vm.searchQuery.deptType.key : vm.searchQuery.deptType;
    		if(statWeek){
    			if(c == 0){
    				c++;
    				url += "?";
    			}else{
    				url += "&";
    			}
    			url += "statWeek="+encodeURI(statWeek);
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
    		if(deptType){
    			if(c == 0){
    				c++;
    				url += "?";
    			}else{
    				url += "&";
    			}
    			url += "deptType="+encodeURI(deptType);
    		}
        	window.open(url);
        }
        
        vm.datePickerOpenStatus.statWeek = false;
        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }    
})();
