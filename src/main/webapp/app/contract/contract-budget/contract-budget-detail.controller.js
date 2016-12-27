(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ContractBudgetDetailController', ContractBudgetDetailController);

    ContractBudgetDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'ContractBudget'];

    function ContractBudgetDetailController($scope, $rootScope, $stateParams, previousState, entity, ContractBudget) {
        var vm = this;

        vm.contractBudget = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('cpmApp:contractBudgetUpdate', function(event, result) {
            vm.contractBudget = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
