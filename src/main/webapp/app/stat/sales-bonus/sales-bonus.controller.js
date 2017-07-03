(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('SalesBonusController', SalesBonusController);

    SalesBonusController.$inject = ['$scope','$rootScope', '$state', 'SalesBonus', 'ParseLinks', 'AlertService', 'paginationConstants', 'pagingParams','DateUtils'];

    function SalesBonusController ($scope,$rootScope, $state, SalesBonus, ParseLinks, AlertService, paginationConstants, pagingParams, DateUtils) {
        var vm = this;
        vm.transition = transition;
        vm.search = search;
        vm.clear = clear;
        vm.exportXls = exportXls;
        
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        var today = new Date();
        if(pagingParams.originYear == undefined){
        	pagingParams.originYear = today.getFullYear();
        }
        if(pagingParams.statWeek == undefined){
        	pagingParams.statWeek = DateUtils.convertLocalDateToFormat(today,"yyyyMMdd");
        }
        vm.searchQuery = {};
        //搜索项中的参数
        vm.searchQuery.originYear= DateUtils.convertYYYYToDate(pagingParams.originYear);
        vm.searchQuery.statWeek= DateUtils.convertDayToDate(pagingParams.statWeek);
        vm.searchQuery.contractId = pagingParams.contractId;
        vm.searchQuery.salesManId = pagingParams.salesManId;
        vm.searchQuery.salesMan = pagingParams.salesMan;
        
        if (!vm.searchQuery.originYear && !vm.searchQuery.statWeek
        		&& !vm.searchQuery.contractId && !vm.searchQuery.salesManId){
        	vm.haveSearch = null;
        }else{
        	vm.haveSearch = true;
        }
        
        vm.contractInfos = [];
        loadContract();
        function loadContract(){
        	SalesBonus.queryContractInfo({
        		
        	},
        	function(data, headers){
        		vm.contractInfos = data;
        		if(vm.contractInfos && vm.contractInfos.length > 0){
        			for(var i = 0; i < vm.contractInfos.length; i++){
        				if(pagingParams.contractId == vm.contractInfos[i].key){
        					vm.searchQuery.contractId = vm.contractInfos[i];
        					angular.element('select[ng-model="vm.searchQuery.contractId"]').parent().find(".select2-chosen").html(vm.contractInfos[i].val);
        				}
        			}
        		}
        	},
        	function(error){
        		AlertService.error(error.data.message);
        	});
        }
        //加载列表数据
        loadAll();
        function loadAll () {
        	SalesBonus.query({
            	originYear:pagingParams.originYear,
            	statWeek:pagingParams.statWeek,
            	contractId:pagingParams.contractId,
            	salesManId:pagingParams.salesManId
            }, onSuccess, onError);
            function onSuccess(data, headers) {
                vm.salesBonuss = handleData(data);
            }
            function onError(error) {
                AlertService.error(error.data.message);
            }
            function handleData(data){
            	return data;
            }
        }

        function transition() {
            $state.transitionTo($state.$current, {
            	originYear: DateUtils.convertLocalDateToFormat(vm.searchQuery.originYear,"yyyy"),
            	statWeek: DateUtils.convertLocalDateToFormat(vm.searchQuery.statWeek,"yyyyMMdd"),
                contractId:vm.searchQuery.contractId && vm.searchQuery.contractId.key? vm.searchQuery.contractId.key : vm.searchQuery.contractId,
              	salesManId:vm.searchQuery.salesManId,
              	salesMan:vm.searchQuery.salesMan
            });
        }

        function search() {
            if (!vm.searchQuery.originYear && !vm.searchQuery.statWeek
            		&& !vm.searchQuery.contractId && !vm.searchQuery.salesManId){
                return vm.clear();
            }
            vm.haveSearch = true;
            vm.transition();
        }

        function clear() {
            vm.searchQuery = {};
        	vm.searchQuery.originYear = new Date();
        	vm.searchQuery.statWeek = new Date();
            vm.haveSearch = true;
            vm.transition();
        }
        function exportXls(){//导出Xls
        	var url = "api/sales-bonus/exportXls";
        	var c = 0;
        	var originYear = DateUtils.convertLocalDateToFormat(vm.searchQuery.originYear,"yyyy");
        	var statWeek = DateUtils.convertLocalDateToFormat(vm.searchQuery.statWeek,"yyyyMMdd");
        	var contractId = vm.searchQuery.contractId && vm.searchQuery.contractId.key? vm.searchQuery.contractId.key : vm.searchQuery.contractId;
			var salesManId = vm.searchQuery.salesManId;
			
			if(originYear){
				if(c == 0){
					c++;
					url += "?";
				}else{
					url += "&";
				}
				url += "originYear="+encodeURI(originYear);
			}
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
			if(salesManId){
				if(c == 0){
					c++;
					url += "?";
				}else{
					url += "&";
				}
				url += "salesManId="+encodeURI(salesManId);
			}
			
        	window.open(url);
        }
        
        vm.datePickerOpenStatus.originYear = false;
        vm.datePickerOpenStatus.statWeek = false;
        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
        
        var unsubscribe = $rootScope.$on('cpmApp:deptInfoSelected', function(event, result) {
        	vm.searchQuery.salesManId = result.objId;
        	vm.searchQuery.salesMan = result.name;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
