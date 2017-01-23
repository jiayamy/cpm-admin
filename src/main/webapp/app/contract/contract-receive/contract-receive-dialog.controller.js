(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ContractReceiveDialogController', ContractReceiveDialogController);

    ContractReceiveDialogController.$inject = ['ContractInfo','previousState','$timeout', '$scope', '$stateParams', 'entity', 'ContractReceive', '$state'];

    function ContractReceiveDialogController (ContractInfo,previousState,$timeout, $scope, $stateParams, entity, ContractReceive, $state) {
        var vm = this;

        vm.contractReceive = entity;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        
        vm.previousState = previousState.name;
        vm.queryDept = previousState.queryDept;
        
        //处理costDay
        vm.contractReceive.receiveDay = DateUtils.convertYYYYMMDDDayToDate(vm.contractReceive.receiveDay);
        loadContractInfos();
        
        //加载合同信息
        function loadContractInfos(){
        	ContractInfo.queryContractInfo(
        		{
        			
        		},
        		function(data, headers){
        			vm.contractInfos = data;
            		if(vm.contractInfos && vm.contractInfos.length > 0){
            			for(var i = 0; i < vm.contractInfos.length; i++){
            				if(entity.contractId == vm.contractInfos[i].key){
            					vm.contractCost.contractId = vm.contractInfos[i];
            				}
            			}
            		}
        		},
        		function(error){
        			AlertService.error(error.data.message);
        			vm.contractInfos = [];
        		}
        	);
        }
        function save () {
            vm.isSaving = true;
            var contractReceive = {};
            contractReceive.contractId = vm.contractReceive.contractId;
            contractReceive.receiveTotal = vm.contractReceive.receiveTotal;
            contractReceive.receiveDay = vm.contractReceive.receiveDay;
            contractReceive.receiver = vm.contractReceive.receiver;
            console.log(contractReceive);
            
            if( !contractReceive.contractId || !contractReceive.receiveTotal || !contractReceive.receiveDay || !contractReceive.receiver || !contractReceive.contractId ){
            	AlertService.error("cpmApp.contractReceive.save.paramNone");
            	return;
            }
            ContractReceive.update(contractReceive,
            	function(data, headers){
            		vm.isSaving = false;
            		if(headers("X-cpmApp-alert") == 'cpmApp.contractReceive.updated'){
            			$state.go(vm.previousState);
            		}
	        	},
	        	function(error){
	        		vm.isSaving = false;
	        	}
            );
            
        }
        vm.datePickerOpenStatus.receiveDay = false;
        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
        //员工模态框
        var unsubscribe = $rootScope.$on('cpmApp:deptInfoSelected', function(event, result) {
        	vm.contractReceive.receiver = result.name;
        	console.log(result);
        });
    }
})();
