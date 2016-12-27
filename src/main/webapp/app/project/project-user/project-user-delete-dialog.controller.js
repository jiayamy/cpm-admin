(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ProjectUserDeleteController',ProjectUserDeleteController);

    ProjectUserDeleteController.$inject = ['$uibModalInstance', 'entity', 'ProjectUser'];

    function ProjectUserDeleteController($uibModalInstance, entity, ProjectUser) {
        var vm = this;

        vm.projectUser = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            ProjectUser.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
