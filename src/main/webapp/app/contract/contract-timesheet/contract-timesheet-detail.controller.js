(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ContractTimesheetDetailController', ContractTimesheetDetailController);

    ContractTimesheetDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'ContractTimesheet'];

    function ContractTimesheetDetailController($scope, $rootScope, $stateParams, previousState, entity, ContractTimesheet) {
        var vm = this;

        vm.contractTimesheet = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('cpmApp:contractTimesheetUpdate', function(event, result) {
            vm.contractTimesheet = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
