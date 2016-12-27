(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ProjectWeeklyStatDialogController', ProjectWeeklyStatDialogController);

    ProjectWeeklyStatDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'ProjectWeeklyStat'];

    function ProjectWeeklyStatDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, ProjectWeeklyStat) {
        var vm = this;

        vm.projectWeeklyStat = entity;
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
            if (vm.projectWeeklyStat.id !== null) {
                ProjectWeeklyStat.update(vm.projectWeeklyStat, onSaveSuccess, onSaveError);
            } else {
                ProjectWeeklyStat.save(vm.projectWeeklyStat, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('cpmApp:projectWeeklyStatUpdate', result);
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
