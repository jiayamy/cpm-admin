(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ProjectCostDeleteController',ProjectCostDeleteController);

    ProjectCostDeleteController.$inject = ['$uibModalInstance', 'entity', 'ProjectCost'];

    function ProjectCostDeleteController($uibModalInstance, entity, ProjectCost) {
        var vm = this;

        vm.projectCost = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            ProjectCost.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
