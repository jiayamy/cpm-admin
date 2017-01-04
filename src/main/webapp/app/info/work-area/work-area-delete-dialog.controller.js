(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('WorkAreaDeleteController',WorkAreaDeleteController);

    WorkAreaDeleteController.$inject = ['$uibModalInstance', 'entity', 'WorkArea'];

    function WorkAreaDeleteController($uibModalInstance, entity, WorkArea) {
        var vm = this;

        vm.workArea = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            WorkArea.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
