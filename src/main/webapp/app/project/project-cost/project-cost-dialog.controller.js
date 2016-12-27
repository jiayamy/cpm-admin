(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ProjectCostDialogController', ProjectCostDialogController);

    ProjectCostDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'ProjectCost'];

    function ProjectCostDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, ProjectCost) {
        var vm = this;

        vm.projectCost = entity;
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
            if (vm.projectCost.id !== null) {
                ProjectCost.update(vm.projectCost, onSaveSuccess, onSaveError);
            } else {
                ProjectCost.save(vm.projectCost, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('cpmApp:projectCostUpdate', result);
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
