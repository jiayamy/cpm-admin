(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ProjectUserDialogController', ProjectUserDialogController);

    ProjectUserDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'ProjectUser'];

    function ProjectUserDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, ProjectUser) {
        var vm = this;

        vm.projectUser = entity;
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
            if (vm.projectUser.id !== null) {
                ProjectUser.update(vm.projectUser, onSaveSuccess, onSaveError);
            } else {
                ProjectUser.save(vm.projectUser, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('cpmApp:projectUserUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.joinDay = false;
        vm.datePickerOpenStatus.goodbyeDay = false;
        vm.datePickerOpenStatus.createTime = false;
        vm.datePickerOpenStatus.updateTime = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
