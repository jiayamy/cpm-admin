(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('OverallBonusController', OverallBonusController);

    OverallBonusController.$inject = ['$scope','$rootScope', '$state', 'OverallBonus','ContractInfo', 'ParseLinks', 'AlertService', 'paginationConstants', 'pagingParams','DateUtils'];

    function OverallBonusController ($scope,$rootScope, $state, OverallBonus,ContractInfo, ParseLinks, AlertService, paginationConstants, pagingParams,DateUtils) {
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
        if (!vm.searchQuery.statWeek && !vm.searchQuery.contractId){
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
        	OverallBonus.query({
        		statWeek: pagingParams.statWeek,
        		contractId: pagingParams.contractId,
        		page: pagingParams.page - 1,
                size: vm.itemsPerPage
            }, onSuccess, onError);
        	function onSuccess(data, headers) {
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
                vm.queryCount = vm.totalItems;
                vm.overallBonuses = handleData(data);
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
                contractId: vm.searchQuery.contractId ? vm.searchQuery.contractId.key: ""
            });
        }
        
        function search() {
            if (!vm.searchQuery.statWeek && !vm.searchQuery.contractId){
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
        	var url = "api/overall-bonus/exportXls";
        	var c = 0;
        	var statWeek = DateUtils.convertLocalDateToFormat(vm.searchQuery.statWeek,"yyyyMMdd");
        	var contractId = vm.searchQuery.contractId && vm.searchQuery.contractId.key? vm.searchQuery.contractId.key : vm.searchQuery.contractId;
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
        	window.open(url);
        }
        
        vm.datePickerOpenStatus.statWeek = false;
        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
