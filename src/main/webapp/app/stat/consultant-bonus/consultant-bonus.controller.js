(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ConsultantBonusController', ConsultantBonusController);

    ConsultantBonusController.$inject = ['ContractInfo','$rootScope', '$scope', '$state', 'DateUtils','ConsultantBonus','ParseLinks', 'AlertService', 'pagingParams'];

    function ConsultantBonusController (ContractInfo,$rootScope,$scope, $state,DateUtils, ConsultantBonus, ParseLinks, AlertService, pagingParams) {
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
        vm.exportXls = exportXls;
        
        vm.searchQuery.fromDate = DateUtils.convertYYYYMMDDDayToDate(pagingParams.fromDate);
        vm.searchQuery.toDate = DateUtils.convertYYYYMMDDDayToDate(pagingParams.toDate);
        vm.searchQuery.contractId = pagingParams.contractId;
        vm.searchQuery.consultantMan = pagingParams.consultantMan;
        vm.searchQuery.consultantManId = pagingParams.consultantManId;
        vm.contractInfos = [];
        
        if (!vm.searchQuery.contractId && !vm.searchQuery.consultantManId && !vm.searchQuery.fromDate && !vm.searchQuery.toDate){
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
                consultantManId : pagingParams.consultantManId,
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
                consultantManId:vm.searchQuery.consultantManId,
                consultantMan:vm.searchQuery.consultantMan,
                fromDate:vm.searchQuery.fromDate ? DateUtils.convertLocalDateToFormat(vm.searchQuery.fromDate,"yyyyMMdd"):"",
                toDate:vm.searchQuery.toDate ? DateUtils.convertLocalDateToFormat(vm.searchQuery.toDate,"yyyyMMdd"):""
            });
        }

        function search() {
        	if (!vm.searchQuery.contractId && !vm.searchQuery.consultantManId && !vm.searchQuery.fromDate && !vm.searchQuery.toDate){
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
        
        function exportXls(){
        	var url = "api/consultant-bonus/exportXls";
        	var c = 0;
        	var fromDate = DateUtils.convertLocalDateToFormat(vm.searchQuery.fromDate,"yyyyMMdd");
        	var toDate = DateUtils.convertLocalDateToFormat(vm.searchQuery.toDate,"yyyyMMdd");
        	var contractId = vm.searchQuery.contractId && vm.searchQuery.contractId.key? vm.searchQuery.contractId.key : vm.searchQuery.contractId;
			var consultantManId = vm.searchQuery.consultantManId;
			
			if(fromDate){
				if(c == 0){
					c++;
					url += "?";
				}else{
					url += "&";
				}
				url += "fromDate="+encodeURI(fromDate);
			}
			if(toDate){
				if(c == 0){
					c++;
					url += "?";
				}else{
					url += "&";
				}
				url += "toDate="+encodeURI(toDate);
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
			if(consultantManId){
				if(c == 0){
					c++;
					url += "?";
				}else{
					url += "&";
				}
				url += "consultantManId="+encodeURI(consultantManId);
			}
			
        	window.open(url);
        }
        
        vm.datePickerOpenStatus = {};
        vm.datePickerOpenStatus.fromDate = false;
        vm.datePickerOpenStatus.toDate = false;
        vm.openCalendar = openCalendar;
        function openCalendar(data){
        	vm.datePickerOpenStatus[data] = true;
        }
        
        var unsubscribe = $rootScope.$on('cpmApp:deptInfoSelected', function(event, result) {
        	vm.searchQuery.consultantManId = result.objId;
        	vm.searchQuery.consultantMan = result.name;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
