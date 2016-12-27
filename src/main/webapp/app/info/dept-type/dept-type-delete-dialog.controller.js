(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('DeptTypeDeleteController',DeptTypeDeleteController);

    DeptTypeDeleteController.$inject = ['$uibModalInstance', 'entity', 'DeptType'];

    function DeptTypeDeleteController($uibModalInstance, entity, DeptType) {
        var vm = this;

        vm.deptType = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            DeptType.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
