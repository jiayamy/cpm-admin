(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('UserTimesheetDialogController', UserTimesheetDialogController);

    UserTimesheetDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'UserTimesheet'];

    function UserTimesheetDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, UserTimesheet) {
        var vm = this;

        vm.userTimesheet = entity;
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
            if (vm.userTimesheet.id !== null) {
                UserTimesheet.update(vm.userTimesheet, onSaveSuccess, onSaveError);
            } else {
                UserTimesheet.save(vm.userTimesheet, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('cpmApp:userTimesheetUpdate', result);
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
