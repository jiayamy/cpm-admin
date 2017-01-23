(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ContractBudgetDialogController', ContractBudgetDialogController);

    ContractBudgetDialogController.$inject = ['$timeout', '$state', '$rootScope','$scope', '$stateParams','previousState', 'entity', 'ContractBudget','AlertService'];

    function ContractBudgetDialogController ($timeout, $state, $rootScope,$scope, $stateParams,previousState, entity, ContractBudget,AlertService) {
        var vm = this;
      
        vm.previousState = previousState.name;
        vm.queryDept = previousState.queryDept;
        
        vm.contractBudget = entity;
        
        vm.save = save;
        
        vm.purchaseTypes = [{ id: 1, name: '硬件' }, { id: 2, name: '软件' }, { id: 3, name: '服务'}];
        
        for(var j = 0; j < vm.purchaseTypes.length; j++){
        	if(vm.contractBudget.purchaseType == vm.purchaseTypes[j].id){
        		vm.contractBudget.purchaseType = vm.purchaseTypes[j];
        	}
        }
        loadContract();
        function loadContract(){
        	ContractBudget.queryUserContract({
        		
	        	},
	        	function(data, headers){
	        		vm.contractInfos = data;
	        		if(vm.contractInfos && vm.contractInfos.length > 0){
	        			var select = false;
	        			for(var i = 0; i < vm.contractInfos.length; i++){
	        				if(entity.contractId == vm.contractInfos[i].key){
	        					vm.contractBudget.contractId = vm.contractInfos[i];
	        					select = true;
	        				}
	        			}
	        			if(!select){
	        				vm.contractBudget.contractId = vm.contractInfos[0];
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
            
           var contractBudget = {};
           contractBudget.id = vm.contractBudget.id;
           contractBudget.contractId = vm.contractBudget.contractId;
           contractBudget.userId = vm.contractBudget.userId;
           contractBudget.userName = vm.contractBudget.userName;
           contractBudget.serialNum = vm.contractBudget.serialNum;
           contractBudget.budgetTotal = vm.contractBudget.budgetTotal;
           contractBudget.name = vm.contractBudget.name;
           contractBudget.purchaseType = vm.contractBudget.purchaseType ? vm.contractBudget.purchaseType.id : "";
           contractBudget.dept = vm.contractBudget.dept;
           contractBudget.deptId = vm.contractBudget.deptId;
           contractBudget.status = vm.contractBudget.status;
           if(contractBudget.contractId && contractBudget.contractId.key){
        	   contractBudget.contractId = contractBudget.contractId.key;
           }
           ContractBudget.update(contractBudget, onSaveSuccess, onSaveError);
        }

        function onSaveSuccess (result) {
            $scope.$emit('cpmApp:contractBudgetUpdate', result);
            $state.go('contract-budget');
            vm.isSaving = false;
        }

        function onSaveError (result) {
            vm.isSaving = false;
        }

        var unsubscribe = $rootScope.$on('cpmApp:deptInfoSelected', function(event, result) {
        	vm.contractBudget.userId = result.objId;
        	vm.contractBudget.userName = result.name;
        	vm.contractBudget.deptId = result.parentId;
        	vm.contractBudget.dept = result.parentName;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
