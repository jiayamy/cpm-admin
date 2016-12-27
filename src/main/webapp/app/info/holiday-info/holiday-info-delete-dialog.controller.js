(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('HolidayInfoDeleteController',HolidayInfoDeleteController);

    HolidayInfoDeleteController.$inject = ['$uibModalInstance', 'entity', 'HolidayInfo'];

    function HolidayInfoDeleteController($uibModalInstance, entity, HolidayInfo) {
        var vm = this;

        vm.holidayInfo = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            HolidayInfo.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
