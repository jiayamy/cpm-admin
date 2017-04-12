(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('SaleWeeklyStatDetailController', SaleWeeklyStatDetailController);

    SaleWeeklyStatDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'SaleWeeklyStat'];

    function SaleWeeklyStatDetailController($scope, $rootScope, $stateParams, previousState, entity, SaleWeeklyStat) {
        var vm = this;

        vm.saleWeeklyStat = entity;
        vm.previousState = previousState.name;
    }
})();
