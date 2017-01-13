(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ContractInfofinRateController', ContractInfofinRateController);

    ContractInfofinRateController.$inject = ['$rootScope','$timeout', '$scope', '$stateParams','entity', 'ContractInfo','$state'];

    function ContractInfofinRateController ($rootScope,$timeout, $scope, $stateParams, entity, ContractInfo, $state) {
        var vm = this;

        vm.contractInfo = entity;
        vm.clear = clear;
        vm.save = save;
        
        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
        	$state.go('contract-info', null, { reload: 'contract-info' });
        }

        function save () {
            vm.isSaving = true;
            if (vm.contractInfo.id !== null) {
                ContractInfo.update(vm.contractInfo, onSaveSuccess, onSaveError);
            } else {
                ContractInfo.save(vm.contractInfo, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('cpmApp:contractInfoUpdate', result);
        	$state.go('contract-info');
        	vm.isSaving = false;
           
        }

        function onSaveError () {
            vm.isSaving = false;
            $state.go('contract-info');
        }
        //部门的处理
        var unsubscribe = $rootScope.$on('cpmApp:deptInfoSelected', function(event, result) {
        	vm.contractInfo.salesman = result.name;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
