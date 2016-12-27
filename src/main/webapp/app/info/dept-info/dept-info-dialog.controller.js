(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('DeptInfoDialogController', DeptInfoDialogController);

    DeptInfoDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'DeptInfo'];

    function DeptInfoDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, DeptInfo) {
        var vm = this;

        vm.deptInfo = entity;
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
            if (vm.deptInfo.id !== null) {
                DeptInfo.update(vm.deptInfo, onSaveSuccess, onSaveError);
            } else {
                DeptInfo.save(vm.deptInfo, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('cpmApp:deptInfoUpdate', result);
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
