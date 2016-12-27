(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ProjectInfoDialogController', ProjectInfoDialogController);

    ProjectInfoDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'ProjectInfo'];

    function ProjectInfoDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, ProjectInfo) {
        var vm = this;

        vm.projectInfo = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.projectInfo.id !== null) {
                ProjectInfo.update(vm.projectInfo, onSaveSuccess, onSaveError);
            } else {
                ProjectInfo.save(vm.projectInfo, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('cpmApp:projectInfoUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.startDay = false;
        vm.datePickerOpenStatus.endDay = false;
        vm.datePickerOpenStatus.createTime = false;
        vm.datePickerOpenStatus.updateTime = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
