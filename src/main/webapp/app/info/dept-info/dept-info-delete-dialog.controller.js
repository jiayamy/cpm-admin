(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('DeptInfoDeleteController',DeptInfoDeleteController);

    DeptInfoDeleteController.$inject = ['$uibModalInstance', 'entity', 'DeptInfo'];

    function DeptInfoDeleteController($uibModalInstance, entity, DeptInfo) {
        var vm = this;

        vm.deptInfo = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            DeptInfo.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
