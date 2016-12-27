(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ProjectInfoDeleteController',ProjectInfoDeleteController);

    ProjectInfoDeleteController.$inject = ['$uibModalInstance', 'entity', 'ProjectInfo'];

    function ProjectInfoDeleteController($uibModalInstance, entity, ProjectInfo) {
        var vm = this;

        vm.projectInfo = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            ProjectInfo.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
