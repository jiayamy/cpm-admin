(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ContractBudgetDialogController', ContractBudgetDialogController);

    ContractBudgetDialogController.$inject = ['$timeout', '$scope', '$stateParams','previousState', 'entity', 'ContractBudget'];

    function ContractBudgetDialogController ($timeout, $scope, $stateParams,previousState, entity, ContractBudget) {
        var vm = this;
        
        vm.previousState = previousState.name;
        vm.contractBudget = entity;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function save () {
            vm.isSaving = true;
            if (vm.contractBudget.id !== null) {
                ContractBudget.update(vm.contractBudget, onSaveSuccess, onSaveError);
            } else {
                ContractBudget.save(vm.contractBudget, onSaveSuccess, onSaveError);
            }
        }

//        function onSaveSuccess (result) {
//            $scope.$emit('cpmApp:contractBudgetUpdate', result);
//            $uibModalInstance.close(result);
//            vm.isSaving = false;
//        }
        function onSaveSuccess (result) {
        	vm.isSaving = false;
        	if(result.data){
        		AlertService.error(result.data.message);
        	}else if(result.message){
        		if(result.message == "cpmApp.contranctBudget.save.success"){
        			AlertService.success(result.message);
        			$state.go("user-timesheet", null, { reload: true });
        		}else if(result.message && result.param){
        			var param = {};
        			param.param = result.param;
        			AlertService.error(result.message,param);
        		}else{
        			AlertService.error(result.message);
        		}
        	}
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.createTime = false;
        vm.datePickerOpenStatus.updateTime = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
