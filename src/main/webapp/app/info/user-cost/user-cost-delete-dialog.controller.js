(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('UserCostDeleteController',UserCostDeleteController);

    UserCostDeleteController.$inject = ['$uibModalInstance', 'entity', 'UserCost'];

    function UserCostDeleteController($uibModalInstance, entity, UserCost) {
        var vm = this;

        vm.userCost = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            UserCost.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
