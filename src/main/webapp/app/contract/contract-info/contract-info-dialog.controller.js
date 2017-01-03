(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ContractInfoDialogController', ContractInfoDialogController);

    ContractInfoDialogController.$inject = ['$timeout', '$scope', '$stateParams','entity', 'ContractInfo','$state'];

    function ContractInfoDialogController ($timeout, $scope, $stateParams, entity, ContractInfo, $state) {
        var vm = this;

        vm.contractInfo = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
        	$state.go('contract-info', null, { reload: 'contract-info' });
        }

        function save () {
            vm.isSaving = true;
            if (vm.contractInfo.id !== null) {
                ContractInfo.update(vm.contractInfo, onSaveSuccess, onSaveError);
            } else {
                ContractInfo.save(vm.contractInfo, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('cpmApp:contractInfoUpdate', result);
        	$state.go('contract-info');
        	vm.isSaving = false;
           
        }

        function onSaveError () {
            vm.isSaving = false;
            $state.go('contract-info');
        }

        vm.datePickerOpenStatus.startDay = false;
        vm.datePickerOpenStatus.endDay = false;
        vm.datePickerOpenStatus.createTime = false;
        vm.datePickerOpenStatus.updateTime = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
