(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('BonusRateDeleteController',BonusRateDeleteController);

    BonusRateDeleteController.$inject = ['$uibModalInstance', 'entity', 'BonusRate'];

    function BonusRateDeleteController($uibModalInstance, entity, BonusRate) {
        var vm = this;

        vm.bonusRate = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
        	BonusRate.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
