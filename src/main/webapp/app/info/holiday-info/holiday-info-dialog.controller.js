(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('HolidayInfoDialogController', HolidayInfoDialogController);

    HolidayInfoDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'HolidayInfo'];

    function HolidayInfoDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, HolidayInfo) {
        var vm = this;

        vm.holidayInfo = entity;
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
            if (vm.holidayInfo.id !== null) {
                HolidayInfo.update(vm.holidayInfo, onSaveSuccess, onSaveError);
            } else {
                HolidayInfo.save(vm.holidayInfo, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('cpmApp:holidayInfoUpdate', result);
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
