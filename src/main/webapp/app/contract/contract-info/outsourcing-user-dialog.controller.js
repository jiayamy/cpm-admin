(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('OutsourcingUserController', OutsourcingUserController);

    OutsourcingUserController.$inject = ['$timeout','$state', '$scope', '$stateParams', 'entity', 'ContractInfo','$uibModalInstance','OutsourcingUser'];

    function OutsourcingUserController ($timeout,$state, $scope, $stateParams, entity, ContractInfo,$uibModalInstance,OutsourcingUser) {
        var vm = this;
        vm.clear = clear;
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
            outsourcingUser.mark = vm.outsourcingUser.mark;
            
            ContractInfo.updateOutsourcingUser(outsourcingUser, onSaveSuccess,onSaveError);
        }
        
	        function onSaveSuccess (result,headers) {
	        	$scope.$emit('cpmApp:viewRecord',result);
	        	if(headers("X-cpmApp-alert") == 'cpmApp.outsourcingUser.updated'){
	        		 $uibModalInstance.close(result);
        		}
	            
	            vm.isSaving = false;
        }

        function onSaveError (error) {
            vm.isSaving = false;
        }
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }
    }
})();
