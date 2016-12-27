(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ProjectWeeklyStatDetailController', ProjectWeeklyStatDetailController);

    ProjectWeeklyStatDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'ProjectWeeklyStat'];

    function ProjectWeeklyStatDetailController($scope, $rootScope, $stateParams, previousState, entity, ProjectWeeklyStat) {
        var vm = this;

        vm.projectWeeklyStat = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('cpmApp:projectWeeklyStatUpdate', function(event, result) {
            vm.projectWeeklyStat = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
