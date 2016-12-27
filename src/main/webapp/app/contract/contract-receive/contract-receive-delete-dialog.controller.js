(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ContractReceiveDeleteController',ContractReceiveDeleteController);

    ContractReceiveDeleteController.$inject = ['$uibModalInstance', 'entity', 'ContractReceive'];

    function ContractReceiveDeleteController($uibModalInstance, entity, ContractReceive) {
        var vm = this;

        vm.contractReceive = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            ContractReceive.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
