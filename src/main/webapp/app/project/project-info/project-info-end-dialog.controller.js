(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ProjectInfoEndController',ProjectInfoEndController);

    ProjectInfoEndController.$inject = ['$uibModalInstance', 'entity', 'ProjectInfo'];

    function ProjectInfoEndController($uibModalInstance, entity, ProjectInfo) {
        var vm = this;

        vm.projectInfo = entity;
        vm.clear = clear;
        vm.confirmEnd = confirmEnd;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmEnd (id) {
            ProjectInfo.end({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
