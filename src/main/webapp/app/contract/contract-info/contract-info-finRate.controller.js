(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ContractInfofinRateController', ContractInfofinRateController);

    ContractInfofinRateController.$inject = ['$rootScope','$timeout', '$scope', '$stateParams','entity', 'ContractInfo','$state'];

    function ContractInfofinRateController ($rootScope,$timeout, $scope, $stateParams, entity, ContractInfo, $state) {
        var vm = this;

        vm.contractInfo = entity;
        vm.save = save;
        
        vm.previousState = "contract-info";

        function save () {
            vm.isSaving = true;
            var contractInfo = {};
            contractInfo.id = vm.contractInfo.id;
            contractInfo.finishRate = vm.contractInfo.finishRate;
          	ContractInfo.finish(contractInfo, onSaveSuccess, onSaveError);
        }

        function onSaveSuccess (result) {
        	$state.go('contract-info');
        	vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }
    }
})();
