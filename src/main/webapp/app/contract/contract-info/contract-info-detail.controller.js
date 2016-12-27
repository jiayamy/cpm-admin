(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ContractInfoDetailController', ContractInfoDetailController);

    ContractInfoDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'ContractInfo'];

    function ContractInfoDetailController($scope, $rootScope, $stateParams, previousState, entity, ContractInfo) {
        var vm = this;

        vm.contractInfo = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('cpmApp:contractInfoUpdate', function(event, result) {
            vm.contractInfo = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
