(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ProjectInfoFinishController',ProjectInfoFinishController);

    ProjectInfoFinishController.$inject = ['$uibModalInstance', 'entity', 'ProjectInfo'];

    function ProjectInfoFinishController($uibModalInstance, entity, ProjectInfo) {
        var vm = this;

        vm.projectInfo = entity;
        vm.clear = clear;
        vm.confirmFinish = confirmFinish;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmFinish (id) {
            ProjectInfo.finish({id: id,finishRate:vm.projectInfo.finishRate},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
