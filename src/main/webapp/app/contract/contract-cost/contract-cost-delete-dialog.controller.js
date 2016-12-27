(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ContractCostDeleteController',ContractCostDeleteController);

    ContractCostDeleteController.$inject = ['$uibModalInstance', 'entity', 'ContractCost'];

    function ContractCostDeleteController($uibModalInstance, entity, ContractCost) {
        var vm = this;

        vm.contractCost = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            ContractCost.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
