(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ProjectTimesheetDeleteController',ProjectTimesheetDeleteController);

    ProjectTimesheetDeleteController.$inject = ['$uibModalInstance', 'entity', 'ProjectTimesheet'];

    function ProjectTimesheetDeleteController($uibModalInstance, entity, ProjectTimesheet) {
        var vm = this;

        vm.projectTimesheet = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            ProjectTimesheet.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
