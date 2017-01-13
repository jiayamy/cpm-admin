(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ContractMonthlyStatDetailController', ContractMonthlyStatDetailController);

    ContractMonthlyStatDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'ContractMonthlyStat'];

    function ContractMonthlyStatDetailController($scope, $rootScope, $stateParams, previousState, entity, ContractMonthlyStat) {
        var vm = this;

        vm.contractMonthlyStat = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('cpmApp:contractMonthlyStatUpdate', function(event, result) {
            vm.contractMonthlyStat = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
