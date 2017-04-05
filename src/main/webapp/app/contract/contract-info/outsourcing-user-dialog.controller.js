(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('OutsourcingUserController', OutsourcingUserController);

    OutsourcingUserController.$inject = ['$timeout','$state', '$scope', '$stateParams', 'entity', 'ContractInfo','previousState'];

    function OutsourcingUserController ($timeout,$state, $scope, $stateParams, entity, ContractInfo,previousState) {
        var vm = this;
        
        vm.previousState = previousState.name;
        vm.outsourcingUser = entity;
        vm.save = save;
        function save () {
            vm.isSaving = true;
            
            var outsourcingUser = {};
            outsourcingUser.id = vm.outsourcingUser.id;
            outsourcingUser.contractId = vm.outsourcingUser.contractId;
            outsourcingUser.rank = vm.outsourcingUser.rank;
            outsourcingUser.offer = vm.outsourcingUser.offer;
            outsourcingUser.targetAmount = vm.outsourcingUser.targetAmount;
            
            ContractInfo.updateOutsourcingUser(outsourcingUser, onSaveSuccess,onSaveError);
        }
        
        function onSaveSuccess (result,headers) {
            if(headers("X-cpmApp-alert") == 'cpmApp.outsourcingUser.updated'){
    			$state.go(vm.previousState);
    		}
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

    }
})();
