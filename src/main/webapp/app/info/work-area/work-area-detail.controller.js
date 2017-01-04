(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('WorkAreaDetailController', WorkAreaDetailController);

    WorkAreaDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'WorkArea'];

    function WorkAreaDetailController($scope, $rootScope, $stateParams, previousState, entity, WorkArea) {
        var vm = this;

        vm.workArea = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('cpmApp:workAreaUpdate', function(event, result) {
            vm.workArea = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
