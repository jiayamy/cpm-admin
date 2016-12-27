(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('DeptTypeDetailController', DeptTypeDetailController);

    DeptTypeDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'DeptType'];

    function DeptTypeDetailController($scope, $rootScope, $stateParams, previousState, entity, DeptType) {
        var vm = this;

        vm.deptType = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('cpmApp:deptTypeUpdate', function(event, result) {
            vm.deptType = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
