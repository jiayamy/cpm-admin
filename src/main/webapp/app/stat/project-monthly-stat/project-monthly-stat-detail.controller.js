(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ProjectMonthlyStatDetailController', ProjectMonthlyStatDetailController);

    ProjectMonthlyStatDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'ProjectMonthlyStat'];

    function ProjectMonthlyStatDetailController($scope, $rootScope, $stateParams, previousState, entity, ProjectMonthlyStat) {
        var vm = this;

        vm.projectMonthlyStat = entity;
        vm.previousState = previousState.name;
//        console.log(previousState);
        var unsubscribe = $rootScope.$on('cpmApp:projectMonthlyStatUpdate', function(event, result) {
            vm.projectMonthlyStat = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
