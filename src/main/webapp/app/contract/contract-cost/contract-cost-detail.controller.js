(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ContractCostDetailController', ContractCostDetailController);

    ContractCostDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'ContractCost'];

    function ContractCostDetailController($scope, $rootScope, $stateParams, previousState, entity, ContractCost) {
        var vm = this;

        vm.contractCost = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('cpmApp:contractCostUpdate', function(event, result) {
            vm.contractCost = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
