(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ProductPriceDetailController', ProductPriceDetailController);

    ProductPriceDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'ProductPrice'];

    function ProductPriceDetailController($scope, $rootScope, $stateParams, previousState, entity, ProductPrice) {
        var vm = this;

        vm.productPrice = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('cpmApp:productPriceUpdate', function(event, result) {
            vm.productPrice = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
