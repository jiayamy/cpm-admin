(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ProjectInfoDetailController', ProjectInfoDetailController);

    ProjectInfoDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'ProjectInfo'];

    function ProjectInfoDetailController($scope, $rootScope, $stateParams, previousState, entity, ProjectInfo) {
        var vm = this;

        vm.projectInfo = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('cpmApp:projectInfoUpdate', function(event, result) {
            vm.projectInfo = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
