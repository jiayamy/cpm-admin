(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('DeptTypeDialogController', DeptTypeDialogController);

    DeptTypeDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'DeptType'];

    function DeptTypeDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, DeptType) {
        var vm = this;

        vm.deptType = entity;
        vm.clear = clear;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.deptType.id !== null) {
                DeptType.update(vm.deptType, onSaveSuccess, onSaveError);
            } else {
                DeptType.save(vm.deptType, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('cpmApp:deptTypeUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
