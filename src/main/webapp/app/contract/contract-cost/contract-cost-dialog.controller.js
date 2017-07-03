(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ContractCostDialogController', ContractCostDialogController);

    ContractCostDialogController.$inject = ['AlertService','previousState','DateUtils','ContractBudget','ContractInfo','$timeout', '$scope','$rootScope', '$stateParams', '$state', 'entity', 'ContractCost'];

    function ContractCostDialogController (AlertService,previousState,DateUtils,ContractBudget,ContractInfo,$timeout, $scope,$rootScope, $stateParams, $state, entity, ContractCost) {
        var vm = this;
        vm.previousState = previousState.name;
        vm.queryDept = previousState.queryDept;
        
        vm.contractCost = entity;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        //处理costDay
        vm.contractCost.costDay = DateUtils.convertYYYYMMDDDayToDate(vm.contractCost.costDay);
        //处理类型
    	if(entity.id == null){
        	vm.types = [{key:2,val:'差旅'},{key:3,val:'采购'},{key:4,val:'商务'}];
        }else{
        	vm.types = [{key:1,val:'工时'},{key:2,val:'差旅'},{key:3,val:'采购'},{key:4,val:'商务'}];
        	for(var j = 0; j < vm.types.length ; j++){
        		if(entity.type == vm.types[j].key){
					vm.contractCost.type = vm.types[j];
				}
        	}
        }
        
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
            					angular.element('select[ng-model="vm.contractCost.contractId"]').parent().find(".select2-chosen").html(vm.contractInfos[i].val);
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
            contractCost.name = vm.contractCost.name;
            contractCost.type = vm.contractCost.type&& vm.contractCost.type.key ? vm.contractCost.type.key : "";
            contractCost.costDay = DateUtils.convertLocalDateToFormat(vm.contractCost.costDay,"yyyyMMdd");
            contractCost.total = vm.contractCost.total;
            contractCost.costDesc = vm.contractCost.costDesc;
            contractCost.deptId = vm.contractCost.deptId;
            contractCost.dept = vm.contractCost.dept;
            if(!contractCost.contractId || !contractCost.name || !contractCost.type || !contractCost.costDay || contractCost.total == undefined
            		|| !contractCost.deptId){
            	vm.isSaving = false;
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
        vm.datePickerOpenStatus.costDay = false;
        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
        var unsubscribe = $rootScope.$on('cpmApp:deptInfoSelected', function(event, result) {
        	vm.contractCost.deptId = result.objId;
        	vm.contractCost.dept = result.name;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
