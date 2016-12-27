(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ContractWeeklyStatDeleteController',ContractWeeklyStatDeleteController);

    ContractWeeklyStatDeleteController.$inject = ['$uibModalInstance', 'entity', 'ContractWeeklyStat'];

    function ContractWeeklyStatDeleteController($uibModalInstance, entity, ContractWeeklyStat) {
        var vm = this;

        vm.contractWeeklyStat = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            ContractWeeklyStat.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
