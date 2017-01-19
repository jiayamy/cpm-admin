(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('UserCostDetailController', UserCostDetailController);

    UserCostDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'UserCost'];

    function UserCostDetailController($scope, $rootScope, $stateParams, previousState, entity, UserCost) {
        var vm = this;

        vm.userCost = entity;
        vm.previousState = previousState.name;

//        var unsubscribe = $rootScope.$on('cpmApp:userCostUpdate', function(event, result) {
//            vm.userCost = result;
//        });
//        $scope.$on('$destroy', unsubscribe);
    }
})();
