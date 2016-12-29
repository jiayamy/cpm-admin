(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('UserManagementDialogController',UserManagementDialogController);

    UserManagementDialogController.$inject = ['$stateParams', '$uibModalInstance', 'entity', 'User', 'JhiLanguageService'];

    function UserManagementDialogController ($stateParams, $uibModalInstance, entity, User, JhiLanguageService) {
        var vm = this;

        vm.authorities = ['ROLE_ADMIN','ROLE_USER','ROLE_TIMESHEET','ROLE_INFO','ROLE_INFO_BASIC','ROLE_INFO_USERCOST','ROLE_CONTRACT','ROLE_CONTRACT_BUDGET','ROLE_CONTRACT_COST','ROLE_CONTRACT_FINISH','ROLE_CONTRACT_INFO','ROLE_CONTRACT_PRODUCTPRICE','ROLE_CONTRACT_PURCHASE','ROLE_CONTRACT_RECEIVE','ROLE_CONTRACT_TIMESHEET','ROLE_CONTRACT_USER','ROLE_PROJECT','ROLE_PROJECT_COST','ROLE_PROJECT_FINISH','ROLE_PROJECT_INFO','ROLE_PROJECT_TIMESHEET','ROLE_PROJECT_USER','ROLE_STAT','ROLE_STAT_CONTRACT','ROLE_STAT_PROJECT'];
        vm.clear = clear;
        vm.languages = null;
        vm.save = save;
        vm.user = entity;


        JhiLanguageService.getAll().then(function (languages) {
            vm.languages = languages;
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function onSaveSuccess (result) {
            vm.isSaving = false;
            $uibModalInstance.close(result);
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        function save () {
            vm.isSaving = true;
            if (vm.user.id !== null) {
                User.update(vm.user, onSaveSuccess, onSaveError);
            } else {
                User.save(vm.user, onSaveSuccess, onSaveError);
            }
        }
    }
})();
