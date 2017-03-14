(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ConsultantBonusController', ConsultantBonusController);

    ConsultantBonusController.$inject = ['ContractInfo','$rootScope', '$scope', '$state', 'DateUtils','ConsultantBonus','ParseLinks', 'AlertService','paginationConstants', 'pagingParams'];

    function ConsultantBonusController (ContractInfo,$rootScope,$scope, $state,DateUtils, ConsultantBonus, ParseLinks, AlertService, paginationConstants, pagingParams) {
        var vm = this;

        vm.transition = transition;
        vm.clear = clear;
        vm.search = search;
        vm.loadAll = loadAll;
        vm.searchQuery = {};
        vm.exportXls = exportXls;
        
        var today = new Date();
        if(pagingParams.statWeek == undefined){
        	pagingParams.statWeek = DateUtils.convertLocalDateToFormat(today,"yyyyMMdd");
        }
        
        vm.searchQuery.statWeek = DateUtils.convertYYYYMMDDDayToDate(pagingParams.statWeek);
        vm.searchQuery.contractId = pagingParams.contractId;
        vm.searchQuery.consultantsName = pagingParams.consultantsName;
        vm.searchQuery.consultantsId = pagingParams.consultantsId;
        vm.contractInfos = [];
        
        if (!vm.searchQuery.contractId && !vm.searchQuery.consultantsId && !vm.searchQuery.statWeek){
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
                contractId : pagingParams.contractId,
                consultantsId : pagingParams.consultantsId,
                statWeek : pagingParams.statWeek
            }, onSuccess, onError);
           
            function onSuccess(data, headers) {
                vm.consultantBonuss = data;
            }
            function onError(error) {
                AlertService.error(error.data.message);
            }
        }

        function transition() {
            $state.transitionTo($state.$current, {
                contractId:vm.searchQuery.contractId ? vm.searchQuery.contractId.key : "",
                consultantsId:vm.searchQuery.consultantsId,
                consultantsName:vm.searchQuery.consultantsName,
                statWeek:DateUtils.convertLocalDateToFormat(vm.searchQuery.statWeek,"yyyyMMdd")
            });
        }

        function search() {
        	if (!vm.searchQuery.contractId && !vm.searchQuery.consultantsId && !vm.searchQuery.statWeek){
                return vm.clear();
            }
            vm.links = null;
            vm.haveSearch = true;
            vm.transition();
        }

        function clear() {
            vm.links = null;
            vm.searchQuery = {};
            vm.searchQuery.statWeek = new Date();
            vm.haveSearch = true;
            vm.transition();
        }
        
        function exportXls(){
        	var url = "api/consultant-bonus/exportXls";
        	var c = 0;
        	var statWeek = DateUtils.convertLocalDateToFormat(vm.searchQuery.statWeek,"yyyyMMdd");
        	var contractId = vm.searchQuery.contractId && vm.searchQuery.contractId.key? vm.searchQuery.contractId.key : vm.searchQuery.contractId;
			var consultantsId = vm.searchQuery.consultantsId;
			
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
			if(consultantsId){
				if(c == 0){
					c++;
					url += "?";
				}else{
					url += "&";
				}
				url += "consultantsId="+encodeURI(consultantsId);
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
        	vm.searchQuery.consultantsId = result.objId;
        	vm.searchQuery.consultantsName = result.name;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
