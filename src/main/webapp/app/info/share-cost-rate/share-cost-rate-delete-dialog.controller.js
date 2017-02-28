(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ShareCostRateDeleteController',ShareCostRateDeleteController);

    ShareCostRateDeleteController.$inject = ['$uibModalInstance', 'entity', 'ShareCostRate'];

    function ShareCostRateDeleteController($uibModalInstance, entity, ShareCostRate) {
        var vm = this;

        vm.shareCostRate = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
        	ShareCostRate.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
