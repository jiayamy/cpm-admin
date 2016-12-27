(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ContractTimesheetDialogController', ContractTimesheetDialogController);

    ContractTimesheetDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'ContractTimesheet'];

    function ContractTimesheetDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, ContractTimesheet) {
        var vm = this;

        vm.contractTimesheet = entity;
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
            if (vm.contractTimesheet.id !== null) {
                ContractTimesheet.update(vm.contractTimesheet, onSaveSuccess, onSaveError);
            } else {
                ContractTimesheet.save(vm.contractTimesheet, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('cpmApp:contractTimesheetUpdate', result);
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
