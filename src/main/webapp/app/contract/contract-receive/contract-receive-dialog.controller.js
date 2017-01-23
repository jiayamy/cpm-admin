(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ContractReceiveDialogController', ContractReceiveDialogController);

    ContractReceiveDialogController.$inject = ['ContractInfo','previousState','$timeout', '$scope','$rootScope', '$stateParams', 'entity', 'ContractReceive', '$state', 'DateUtils'];

    function ContractReceiveDialogController (ContractInfo,previousState,$timeout, $scope, $rootScope, $stateParams, entity, ContractReceive, $state, DateUtils) {
        var vm = this;

        vm.contractReceive = entity;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        
        vm.previousState = previousState.name;
        vm.queryDept = previousState.queryDept;
        
        //处理costDay
        vm.contractReceive.receiveDay = DateUtils.convertYYYYMMDDDayToDate(vm.contractReceive.receiveDay);
        console.log(vm.contractReceive);
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
            					vm.contractReceive.contractId = vm.contractInfos[i];
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
            contractReceive.id = vm.contractReceive.id;
            contractReceive.contractId = vm.contractReceive.contractId ? vm.contractReceive.contractId.key : "";
            contractReceive.receiveTotal = vm.contractReceive.receiveTotal;
            contractReceive.receiveDay = DateUtils.convertLocalDateToFormat(vm.contractReceive.receiveDay,"yyyyMMdd");
            contractReceive.receiver = vm.contractReceive.receiver;
            
            if( !contractReceive.contractId || contractReceive.receiveTotal == undefined
            		|| !contractReceive.receiveDay || !contractReceive.receiver){
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
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
