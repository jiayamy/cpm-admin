(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ContractCostDialogController', ContractCostDialogController);

    ContractCostDialogController.$inject = ['AlertService','previousState','DateUtils','ContractBudget','ContractInfo','$timeout', '$scope', '$stateParams', '$state', 'entity', 'ContractCost'];

    function ContractCostDialogController (AlertService,previousState,DateUtils,ContractBudget,ContractInfo,$timeout, $scope, $stateParams, $state, entity, ContractCost) {
        var vm = this;
        vm.previousState = previousState.name;
        vm.contractCost = entity;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.getBudges = getBudges;
        //处理costDay
        vm.contractCost.costDay = DateUtils.convertYYYYMMDDDayToDate(vm.contractCost.costDay);
        //处理类型
        if(entity == null){
        	vm.types = [{key:2,val:'差旅'},{key:3,val:'采购'},{key:4,val:'商务'}];
        }else{
        	vm.types = [{key:1,val:'工时'},{key:2,val:'差旅'},{key:3,val:'采购'},{key:4,val:'商务'}];
        	for(var j = 0;j<vm.types.length; j++){
        		if(entity.type == vm.types[j].key){
        			vm.contractCost.type = vm.types[j];
        		}
        	}
        }
        loadContractInfos();
        if(vm.contractCost.budgetId == null){
        	loadAllBudges();
        	
        }else {
        	getBudges();
		}
        
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
            var contractCost ={};
            
            contractCost.id = vm.contractCost.id;
            contractCost.contractId = vm.contractCost.contractId && vm.contractCost.contractId.key ? vm.contractCost.contractId.key : "";
            contractCost.budgetId = vm.contractCost.budgetId && vm.contractCost.budgetId.key ? vm.contractCost.budgetId.key : "";
            contractCost.name = vm.contractCost.name;
            contractCost.type = vm.contractCost.type&& vm.contractCost.type.key ? vm.contractCost.type.key : "";
            contractCost.costDay = DateUtils.convertLocalDateToFormat(vm.contractCost.costDay,"yyyyMMdd");
            contractCost.total = vm.contractCost.total;
            contractCost.costDesc = vm.contractCost.costDesc;
            
            console.log(contractCost);
            if(!contractCost.contractId || !contractCost.name || !contractCost.type || !contractCost.costDay || contractCost.total == undefined ){
            	AlertService.error("cpmApp.contractCost.save.paramNone");
            	return;
            }
            ContractCost.update(contractCost, 
        		function(data, headers){
            		vm.isSaving = false;
            		if(headers("X-cpmApp-alert") == 'cpmApp.contractCost.updated'){
            			$state.go(vm.previousState);
            		}
	        	},
	        	function(error){
	        		vm.isSaving = false;
	        	}
            );
        }
        //加载预算金额
        function getBudges(){
        	ContractCost.queryContractBudgets(
            		{
            			contractId: vm.contractCost.budgetId
            		},
            		function(data, headers){
            			vm.contractBudgets = data;
                		if(vm.contractBudgets && vm.contractBudgets.length > 0){
                			for(var i = 0; i < vm.contractBudgets.length; i++){
                				if(entity.budgetId == vm.contractBudgets[i].key){
                					vm.contractCost.budgetId = vm.contractBudgets[i];
                				}
                			}
                		}
            		},
            		function(error){
            			AlertService.error(error.data.message);
            			vm.contractBudgets = [];
            		}
            	);
        	
        }
        function loadAllBudges(){
        	ContractCost.queryAllBudges(
            		{
            			
            		},
            		function(data, headers){
            			vm.contractBudgets = data;
            			console.log(vm.contractBudgets);
                		if(vm.contractBudgets && vm.contractBudgets.length > 0){
                			for(var i = 0; i < vm.contractBudgets.length; i++){
                				if(entity.budgetId == vm.contractBudgets[i].key){
                					vm.contractCost.budgetId = vm.contractBudgets[i];
                				}
                			}
                		}
            		},
            		function(error){
            			AlertService.error(error.data.message);
            			vm.contractBudgets = [];
            		}
            	);
        }
        vm.datePickerOpenStatus.costDay = false;
        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
