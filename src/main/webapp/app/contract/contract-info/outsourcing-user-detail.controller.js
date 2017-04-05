(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('OutsourcingUserDetailController', OutsourcingUserDetailController);

    OutsourcingUserDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity'];

    function OutsourcingUserDetailController($scope, $rootScope, $stateParams, previousState, entity) {
        var vm = this;

        vm.outsourcingUser = entity;
        vm.previousState = previousState.name;

    }
})();
