(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ContractBudgetDialogController', ContractBudgetDialogController);

    ContractBudgetDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'ContractBudget'];

    function ContractBudgetDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, ContractBudget) {
        var vm = this;

        vm.contractBudget = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.contractBudget.id !== null) {
                ContractBudget.update(vm.contractBudget, onSaveSuccess, onSaveError);
            } else {
                ContractBudget.save(vm.contractBudget, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('cpmApp:contractBudgetUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
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
