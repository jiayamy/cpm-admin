(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ProjectUserDetailController', ProjectUserDetailController);

    ProjectUserDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'ProjectUser'];

    function ProjectUserDetailController($scope, $rootScope, $stateParams, previousState, entity, ProjectUser) {
        var vm = this;

        vm.projectUser = entity;
        if (vm.projectUser.type == 2) {
			vm.isRank = true;
		}
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('cpmApp:projectUserUpdate', function(event, result) {
            vm.projectUser = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
