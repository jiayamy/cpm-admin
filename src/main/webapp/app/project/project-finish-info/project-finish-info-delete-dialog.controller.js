(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ProjectFinishInfoDeleteController',ProjectFinishInfoDeleteController);

    ProjectFinishInfoDeleteController.$inject = ['$uibModalInstance', 'entity', 'ProjectFinishInfo'];

    function ProjectFinishInfoDeleteController($uibModalInstance, entity, ProjectFinishInfo) {
        var vm = this;

        vm.projectFinishInfo = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            ProjectFinishInfo.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
