(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ProjectTimesheetDetailController', ProjectTimesheetDetailController);

    ProjectTimesheetDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'ProjectTimesheet'];

    function ProjectTimesheetDetailController($scope, $rootScope, $stateParams, previousState, entity, ProjectTimesheet) {
        var vm = this;

        vm.projectTimesheet = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('cpmApp:projectTimesheetUpdate', function(event, result) {
            vm.projectTimesheet = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
