(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('PurchaseItemDialogController', PurchaseItemDialogController);

    PurchaseItemDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'PurchaseItem'];

    function PurchaseItemDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, PurchaseItem) {
        var vm = this;

        vm.purchaseItem = entity;
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
            if (vm.purchaseItem.id !== null) {
                PurchaseItem.update(vm.purchaseItem, onSaveSuccess, onSaveError);
            } else {
                PurchaseItem.save(vm.purchaseItem, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('cpmApp:purchaseItemUpdate', result);
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
