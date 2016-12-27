(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ProjectFinishInfoDetailController', ProjectFinishInfoDetailController);

    ProjectFinishInfoDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'ProjectFinishInfo'];

    function ProjectFinishInfoDetailController($scope, $rootScope, $stateParams, previousState, entity, ProjectFinishInfo) {
        var vm = this;

        vm.projectFinishInfo = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('cpmApp:projectFinishInfoUpdate', function(event, result) {
            vm.projectFinishInfo = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
