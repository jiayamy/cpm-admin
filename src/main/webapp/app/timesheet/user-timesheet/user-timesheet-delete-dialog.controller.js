(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('UserTimesheetDeleteController',UserTimesheetDeleteController);

    UserTimesheetDeleteController.$inject = ['$uibModalInstance', 'entity', 'UserTimesheet'];

    function UserTimesheetDeleteController($uibModalInstance, entity, UserTimesheet) {
        var vm = this;

        vm.userTimesheet = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            UserTimesheet.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
