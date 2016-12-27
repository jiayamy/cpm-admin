(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('PurchaseItemDeleteController',PurchaseItemDeleteController);

    PurchaseItemDeleteController.$inject = ['$uibModalInstance', 'entity', 'PurchaseItem'];

    function PurchaseItemDeleteController($uibModalInstance, entity, PurchaseItem) {
        var vm = this;

        vm.purchaseItem = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            PurchaseItem.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
