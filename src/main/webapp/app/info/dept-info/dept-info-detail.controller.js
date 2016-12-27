(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('DeptInfoDetailController', DeptInfoDetailController);

    DeptInfoDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'DeptInfo'];

    function DeptInfoDetailController($scope, $rootScope, $stateParams, previousState, entity, DeptInfo) {
        var vm = this;

        vm.deptInfo = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('cpmApp:deptInfoUpdate', function(event, result) {
            vm.deptInfo = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
