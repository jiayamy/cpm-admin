(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ProjectCostDetailController', ProjectCostDetailController);

    ProjectCostDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'ProjectCost'];

    function ProjectCostDetailController($scope, $rootScope, $stateParams, previousState, entity, ProjectCost) {
        var vm = this;

        vm.projectCost = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('cpmApp:projectCostUpdate', function(event, result) {
            vm.projectCost = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
