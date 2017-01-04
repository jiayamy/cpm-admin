(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('WorkAreaDialogController', WorkAreaDialogController);

    WorkAreaDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'WorkArea'];

    function WorkAreaDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, WorkArea) {
        var vm = this;

        vm.workArea = entity;
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
            if (vm.workArea.id !== null) {
                WorkArea.update(vm.workArea, onSaveSuccess, onSaveError);
            } else {
                WorkArea.save(vm.workArea, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('cpmApp:workAreaUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
