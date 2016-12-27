(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ContractWeeklyStatDialogController', ContractWeeklyStatDialogController);

    ContractWeeklyStatDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'ContractWeeklyStat'];

    function ContractWeeklyStatDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, ContractWeeklyStat) {
        var vm = this;

        vm.contractWeeklyStat = entity;
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
            if (vm.contractWeeklyStat.id !== null) {
                ContractWeeklyStat.update(vm.contractWeeklyStat, onSaveSuccess, onSaveError);
            } else {
                ContractWeeklyStat.save(vm.contractWeeklyStat, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('cpmApp:contractWeeklyStatUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.createTime = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
