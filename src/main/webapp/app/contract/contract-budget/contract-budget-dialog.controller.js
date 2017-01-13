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
        console.log(vm.contractBudget.purchaseType)
        if(vm.contractBudget.purchaseType == '硬件'){
    		vm.contractBudget.purchaseType = { id: 1, name: '硬件' }; 
    	}else if(vm.contractBudget.purchaseType == '软件'){
    		vm.contractBudget.purchaseType = { id: 2, name: '软件' };
    	}else if (vm.contractBudget.purchaseType == '服务') {
    		vm.contractBudget.purchaseType = { id: 3, name: '服务' };
		}
        console.log(vm.contractBudget.purchaseType);
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        
        vm.purchaseTypes = [{ id: 1, name: '硬件' }, { id: 2, name: '软件' }, { id: 3, name: '服务'}];
        
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
        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function save () {
            vm.isSaving = true;
            
           var contractBudget = {};
           contractBudget.id = vm.contractBudget.id
           contractBudget.contractId = vm.contractBudget.contractId;
           contractBudget.userId = vm.contractBudget.userId;
           contractBudget.serialNum = vm.contractBudget.serialNum;
           contractBudget.budgetTotal = vm.contractBudget.budgetTotal;
           contractBudget.name = vm.contractBudget.name;
           contractBudget.budgetName = vm.contractBudget.budgetName;
           contractBudget.purchaseType = vm.contractBudget.purchaseType;
           contractBudget.userName = vm.contractBudget.userName;
           contractBudget.dept = vm.contractBudget.dept;
           contractBudget.deptId = vm.contractBudget.deptId;
           contractBudget.status = vm.contractBudget.status;
           contractBudget.type = vm.contractBudget.type;
           if(contractBudget.contractId && contractBudget.contractId.key){
        	   contractBudget.contractId = contractBudget.contractId.key;
           }
           
           if (contractBudget.purchaseType) {
        	   contractBudget.purchaseType = contractBudget.purchaseType.id;
           }
           console.log(contractBudget);
           ContractBudget.update(contractBudget, onSaveSuccess, onSaveError);
        }

        function onSaveSuccess (result) {
            $scope.$emit('cpmApp:contractBudgetUpdate', result);
            $state.go('contract-budget');
            vm.isSaving = false;
        }

        function onSaveError (contractBudget) {
            vm.isSaving = false;
            if(contractBudget.purchaseType == 1){
        		contractBudget.purchaseType = { id: 1, name: '硬件' }; 
        	}else if(contractBudget.purchaseType == 2){
        		contractBudget.purchaseType = { id: 2, name: '软件' };
        	}else if (contractBudget.purchaseType == 3) {
        		contractBudget.purchaseType = { id: 3, name: '服务' };
    		}
        }

        vm.datePickerOpenStatus.createTime = false;
        vm.datePickerOpenStatus.updateTime = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
        var unsubscribe = $rootScope.$on('cpmApp:deptInfoSelected', function(event, result) {
        	vm.contractBudget.pmId = result.objId;
        	vm.contractBudget.userName = result.name;
        	vm.contractBudget.userId = result.objId;
        	vm.contractBudget.deptId = result.parentId;
        	vm.contractBudget.dept = result.parentName;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
