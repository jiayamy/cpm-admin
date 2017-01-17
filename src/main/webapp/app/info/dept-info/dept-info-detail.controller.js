(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('DeptInfoDetailController', DeptInfoDetailController);

    DeptInfoDetailController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'DeptInfo'];

    function DeptInfoDetailController($timeout, $scope, $stateParams, $uibModalInstance, entity, DeptInfo) {
        var vm = this;

        vm.deptInfo = entity;
        
        vm.clear = clear;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }
    }
})();
