(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ContractReceiveDialogController', ContractReceiveDialogController);

    ContractReceiveDialogController.$inject = ['$timeout', '$scope', '$stateParams', 'entity', 'ContractReceive', '$state'];

    function ContractReceiveDialogController ($timeout, $scope, $stateParams, entity, ContractReceive, $state) {
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
        	$state.go('contract-receive', null, { reload: 'contract-receive'});
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
            $state.go('contract-receive');
            vm.isSaving = false;
           
        }

        function onSaveError () {
            vm.isSaving = false;
            $state.go('contract-receive');
        }

        vm.datePickerOpenStatus.receiveDay = false;
        vm.datePickerOpenStatus.createTime = false;
        vm.datePickerOpenStatus.updateTime = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
