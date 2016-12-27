(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ContractUserDialogController', ContractUserDialogController);

    ContractUserDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'ContractUser'];

    function ContractUserDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, ContractUser) {
        var vm = this;

        vm.contractUser = entity;
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
            if (vm.contractUser.id !== null) {
                ContractUser.update(vm.contractUser, onSaveSuccess, onSaveError);
            } else {
                ContractUser.save(vm.contractUser, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('cpmApp:contractUserUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.joinDay = false;
        vm.datePickerOpenStatus.leaveDay = false;
        vm.datePickerOpenStatus.createTime = false;
        vm.datePickerOpenStatus.updateTime = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
