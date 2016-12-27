(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('PurchaseItemDetailController', PurchaseItemDetailController);

    PurchaseItemDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'PurchaseItem'];

    function PurchaseItemDetailController($scope, $rootScope, $stateParams, previousState, entity, PurchaseItem) {
        var vm = this;

        vm.purchaseItem = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('cpmApp:purchaseItemUpdate', function(event, result) {
            vm.purchaseItem = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
