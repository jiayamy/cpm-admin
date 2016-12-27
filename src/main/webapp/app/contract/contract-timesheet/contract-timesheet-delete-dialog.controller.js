(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ContractTimesheetDeleteController',ContractTimesheetDeleteController);

    ContractTimesheetDeleteController.$inject = ['$uibModalInstance', 'entity', 'ContractTimesheet'];

    function ContractTimesheetDeleteController($uibModalInstance, entity, ContractTimesheet) {
        var vm = this;

        vm.contractTimesheet = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            ContractTimesheet.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
