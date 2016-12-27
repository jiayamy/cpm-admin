(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ProjectFinishInfoDialogController', ProjectFinishInfoDialogController);

    ProjectFinishInfoDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'ProjectFinishInfo'];

    function ProjectFinishInfoDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, ProjectFinishInfo) {
        var vm = this;

        vm.projectFinishInfo = entity;
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
            if (vm.projectFinishInfo.id !== null) {
                ProjectFinishInfo.update(vm.projectFinishInfo, onSaveSuccess, onSaveError);
            } else {
                ProjectFinishInfo.save(vm.projectFinishInfo, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('cpmApp:projectFinishInfoUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.createTime = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
