(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ContractInfoDeleteController',ContractInfoDeleteController);

    ContractInfoDeleteController.$inject = ['$uibModalInstance', 'entity', 'ContractInfo'];

    function ContractInfoDeleteController($uibModalInstance, entity, ContractInfo) {
        var vm = this;

        vm.contractInfo = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            ContractInfo.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
