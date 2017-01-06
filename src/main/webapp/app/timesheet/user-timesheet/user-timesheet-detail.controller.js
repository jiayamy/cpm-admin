(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('UserTimesheetDetailController', UserTimesheetDetailController);

    UserTimesheetDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'UserTimesheet'];

    function UserTimesheetDetailController($scope, $rootScope, $stateParams, previousState, entity, UserTimesheet) {
        var vm = this;

        vm.userTimesheet = entity;
        vm.previousState = previousState.name;
    }
})();
