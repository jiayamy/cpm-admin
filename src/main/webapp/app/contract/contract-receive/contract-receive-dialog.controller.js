(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ContractReceiveDialogController', ContractReceiveDialogController);

    ContractReceiveDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'ContractReceive'];

    function ContractReceiveDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, ContractReceive) {
        var vm = this;

        vm.contractReceive = entity;
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
            if (vm.contractReceive.id !== null) {
                ContractReceive.update(vm.contractReceive, onSaveSuccess, onSaveError);
            } else {
                ContractReceive.save(vm.contractReceive, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('cpmApp:contractReceiveUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.receiveDay = false;
        vm.datePickerOpenStatus.createTime = false;
        vm.datePickerOpenStatus.updateTime = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
