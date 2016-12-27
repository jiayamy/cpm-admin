(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('HolidayInfoDetailController', HolidayInfoDetailController);

    HolidayInfoDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'HolidayInfo'];

    function HolidayInfoDetailController($scope, $rootScope, $stateParams, previousState, entity, HolidayInfo) {
        var vm = this;

        vm.holidayInfo = entity;
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('cpmApp:holidayInfoUpdate', function(event, result) {
            vm.holidayInfo = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
