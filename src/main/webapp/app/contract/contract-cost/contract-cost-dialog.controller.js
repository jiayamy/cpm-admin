(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ContractCostDialogController', ContractCostDialogController);

    ContractCostDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$state', 'entity', 'ContractCost'];

    function ContractCostDialogController ($timeout, $scope, $stateParams, $state, entity, ContractCost) {
        var vm = this;

        vm.contractCost = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
        	$state.go('contract-cost', null, { reload: 'contract-cost' });
        }

        function save () {
            vm.isSaving = true;
            if (vm.contractCost.id !== null) {
                ContractCost.update(vm.contractCost, onSaveSuccess, onSaveError);
            } else {
                ContractCost.save(vm.contractCost, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('cpmApp:contractCostUpdate', result);
            $state.go('contract-cost', null, { reload: 'contract-cost' });
            vm.isSaving = false;
        }

        function onSaveError () {
        	$state.go('contract-cost');
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.createTime = false;
        vm.datePickerOpenStatus.updateTime = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
