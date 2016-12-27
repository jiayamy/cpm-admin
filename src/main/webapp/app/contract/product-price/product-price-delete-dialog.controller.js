(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ProductPriceDeleteController',ProductPriceDeleteController);

    ProductPriceDeleteController.$inject = ['$uibModalInstance', 'entity', 'ProductPrice'];

    function ProductPriceDeleteController($uibModalInstance, entity, ProductPrice) {
        var vm = this;

        vm.productPrice = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            ProductPrice.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
