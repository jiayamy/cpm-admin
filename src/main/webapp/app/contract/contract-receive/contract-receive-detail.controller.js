(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ContractReceiveDetailController', ContractReceiveDetailController);

    ContractReceiveDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'ContractReceive'];

    function ContractReceiveDetailController($scope, $rootScope, $stateParams, previousState, entity, ContractReceive) {
        var vm = this;

        vm.contractReceive = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('cpmApp:contractReceiveUpdate', function(event, result) {
            vm.contractReceive = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
