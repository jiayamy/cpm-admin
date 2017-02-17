(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ConsultantBonusController', ConsultantBonusController);

    ConsultantBonusController.$inject = ['ContractInfo','$scope', '$state', 'DateUtils','ConsultantBonus','ParseLinks', 'AlertService', 'paginationConstants', 'pagingParams'];

    function ConsultantBonusController (ContractInfo,$scope, $state,DateUtils, ConsultantBonus, ParseLinks, AlertService, paginationConstants, pagingParams) {
        var vm = this;

        vm.loadPage = loadPage;
        vm.predicate = pagingParams.predicate;
        vm.reverse = pagingParams.ascending;
        vm.transition = transition;
        vm.itemsPerPage = 10;
        vm.clear = clear;
        vm.search = search;
        vm.loadAll = loadAll;
        vm.searchQuery = {};
        vm.searchQuery.fromDate = DateUtils.convertYYYYMMDDDayToDate(pagingParams.fromDate);
        vm.searchQuery.toDate = DateUtils.convertYYYYMMDDDayToDate(pagingParams.toDate);
        vm.searchQuery.contractId = pagingParams.contractId;
        vm.contractInfos = [];
        
        if (!vm.searchQuery.contractId && !vm.searchQuery.fromDate && !vm.searchQuery.toDate){
        	vm.haveSearch = null;
        }else{
        	vm.haveSearch = true;
        }
        loadConsultantBonus();
        function loadConsultantBonus(){
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
        
        loadAll();

        function loadAll () {
        	ConsultantBonus.query({
                page: pagingParams.page - 1,
                size: vm.itemsPerPage,
                sort: sort(),
                contractId : pagingParams.contractId,
                fromDate : pagingParams.fromDate,
                toDate : pagingParams.toDate
            }, onSuccess, onError);
           
            function sort() {
                var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
                if (vm.predicate !== 'm.id') {
                    result.push('m.id');
                }
                return result;
            }
            function onSuccess(data, headers) {
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
                vm.queryCount = vm.totalItems;
                vm.consultantBonuss = data;
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
                contractId:vm.searchQuery.contractId ? vm.searchQuery.contractId.key : "",
                fromDate:vm.searchQuery.fromDate ? DateUtils.convertLocalDateToFormat(vm.searchQuery.fromDate,"yyyyMMdd"):"",
                toDate:vm.searchQuery.toDate ? DateUtils.convertLocalDateToFormat(vm.searchQuery.toDate,"yyyyMMdd"):""
            });
        }

        function search() {
        	console.log("---"+vm.searchQuery.fromDate);
        	if (!vm.searchQuery.contractId && !vm.searchQuery.fromDate && !vm.searchQuery.toDate){
                return vm.clear();
            }
            vm.links = null;
            vm.page = 1;
            vm.predicate = 'm.id';
            vm.reverse = false;
            vm.haveSearch = true;
            vm.transition();
        }

        function clear() {
            vm.links = null;
            vm.page = 1;
            vm.predicate = 'm.id';
            vm.reverse = true;
            vm.searchQuery = {};
            vm.haveSearch = false;
            vm.transition();
        }
        
        vm.datePickerOpenStatus = {};
        vm.datePickerOpenStatus.fromDate = false;
        vm.datePickerOpenStatus.toDate = false;
        vm.openCalendar = openCalendar;
        function openCalendar(data){
        	vm.datePickerOpenStatus[data] = true;
        }
    }
})();
