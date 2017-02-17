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
        
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        
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
            vm.haveSearch = null;
            vm.transition();
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
