(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ContractWeeklyStatDetailController', ContractWeeklyStatDetailController);

    ContractWeeklyStatDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'ContractWeeklyStat'];

    function ContractWeeklyStatDetailController($scope, $rootScope, $stateParams, previousState, entity, ContractWeeklyStat) {
        var vm = this;

        vm.contractWeeklyStat = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('cpmApp:contractWeeklyStatUpdate', function(event, result) {
            vm.contractWeeklyStat = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
