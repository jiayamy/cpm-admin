(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ProductPriceDialogController', ProductPriceDialogController);

    ProductPriceDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'ProductPrice'];

    function ProductPriceDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, ProductPrice) {
        var vm = this;

        vm.productPrice = entity;
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
            if (vm.productPrice.id !== null) {
                ProductPrice.update(vm.productPrice, onSaveSuccess, onSaveError);
            } else {
                ProductPrice.save(vm.productPrice, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('cpmApp:productPriceUpdate', result);
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
