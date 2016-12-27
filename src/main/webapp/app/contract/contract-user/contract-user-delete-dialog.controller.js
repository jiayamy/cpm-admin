(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ContractUserDeleteController',ContractUserDeleteController);

    ContractUserDeleteController.$inject = ['$uibModalInstance', 'entity', 'ContractUser'];

    function ContractUserDeleteController($uibModalInstance, entity, ContractUser) {
        var vm = this;

        vm.contractUser = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            ContractUser.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
