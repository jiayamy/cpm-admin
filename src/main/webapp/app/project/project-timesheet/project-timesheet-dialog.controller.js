(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ProjectTimesheetDialogController', ProjectTimesheetDialogController);

    ProjectTimesheetDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'ProjectTimesheet'];

    function ProjectTimesheetDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, ProjectTimesheet) {
        var vm = this;

        vm.projectTimesheet = entity;
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
            if (vm.projectTimesheet.id !== null) {
                ProjectTimesheet.update(vm.projectTimesheet, onSaveSuccess, onSaveError);
            } else {
                ProjectTimesheet.save(vm.projectTimesheet, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('cpmApp:projectTimesheetUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.createTime = false;
        vm.datePickerOpenStatus.updateTime = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
