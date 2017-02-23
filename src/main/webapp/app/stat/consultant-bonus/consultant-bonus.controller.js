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
        
        vm.searchQuery.statWeek = DateUtils.convertYYYYMMDDDayToDate(pagingParams.statWeek);
        vm.searchQuery.contractId = pagingParams.contractId;
        vm.searchQuery.consultantsName = pagingParams.consultantsName;
        vm.searchQuery.consultantsNameId = pagingParams.consultantsNameId;
        vm.contractInfos = [];
        
        if (!vm.searchQuery.contractId && !vm.searchQuery.consultantManId && !vm.searchQuery.statWeek){
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
                consultantsNameId : pagingParams.consultantsNameId,
                statWeek : pagingParams.statWeek
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
                consultantsNameId:vm.searchQuery.consultantsNameId,
                consultantsName:vm.searchQuery.consultantsName,
                statWeek:vm.searchQuery.statWeek ? DateUtils.convertLocalDateToFormat(vm.searchQuery.statWeek,"yyyyMMdd"):""
            });
        }

        function search() {
        	if (!vm.searchQuery.contractId && !vm.searchQuery.consultantsNameId && !vm.searchQuery.statWeek){
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
        	var statWeek = DateUtils.convertLocalDateToFormat(vm.searchQuery.statWeek,"yyyyMMdd");
        	var contractId = vm.searchQuery.contractId && vm.searchQuery.contractId.key? vm.searchQuery.contractId.key : vm.searchQuery.contractId;
			var consultantsNameId = vm.searchQuery.consultantsNameId;
			
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
			if(consultantsNameId){
				if(c == 0){
					c++;
					url += "?";
				}else{
					url += "&";
				}
				url += "consultantsNameId="+encodeURI(consultantsNameId);
			}
			
        	window.open(url);
        }
        
        vm.datePickerOpenStatus = {};
        vm.datePickerOpenStatus.statWeek = false;
        vm.openCalendar = openCalendar;
        function openCalendar(data){
        	vm.datePickerOpenStatus[data] = true;
        }
        
        var unsubscribe = $rootScope.$on('cpmApp:deptInfoSelected', function(event, result) {
        	vm.searchQuery.consultantsNameId = result.objId;
        	vm.searchQuery.consultantsName = result.name;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
