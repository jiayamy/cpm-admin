(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ContractBudgetDeleteController',ContractBudgetDeleteController);

    ContractBudgetDeleteController.$inject = ['$uibModalInstance', 'entity', 'ContractBudget'];

    function ContractBudgetDeleteController($uibModalInstance, entity, ContractBudget) {
        var vm = this;

        vm.contractBudget = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            ContractBudget.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
