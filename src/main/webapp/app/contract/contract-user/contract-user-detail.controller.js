(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ContractUserDetailController', ContractUserDetailController);

    ContractUserDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'ContractUser'];

    function ContractUserDetailController($scope, $rootScope, $stateParams, previousState, entity, ContractUser) {
        var vm = this;

        vm.contractUser = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('cpmApp:contractUserUpdate', function(event, result) {
            vm.contractUser = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
