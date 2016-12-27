(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ProjectWeeklyStatDeleteController',ProjectWeeklyStatDeleteController);

    ProjectWeeklyStatDeleteController.$inject = ['$uibModalInstance', 'entity', 'ProjectWeeklyStat'];

    function ProjectWeeklyStatDeleteController($uibModalInstance, entity, ProjectWeeklyStat) {
        var vm = this;

        vm.projectWeeklyStat = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            ProjectWeeklyStat.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
