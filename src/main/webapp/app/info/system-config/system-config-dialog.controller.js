(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('SystemConfigDialogController', SystemConfigDialogController);

    SystemConfigDialogController.$inject = ['previousState', '$stateParams', '$state', 'entity', 'SystemConfig'];

    function SystemConfigDialogController (previousState, $stateParams, $state, entity, SystemConfig) {
        var vm = this;
        vm.previousState = previousState.name;
        
        vm.systemConfig = entity;
        vm.save = save;
        
        function save () {
            vm.isSaving = true;
            var systemConfig ={};
            
            systemConfig.id = vm.systemConfig.id;
            systemConfig.key = vm.systemConfig.key;
            systemConfig.value = vm.systemConfig.value;
            systemConfig.description = vm.systemConfig.description;
            
            SystemConfig.update(systemConfig, 
        		function(data, headers){
            		vm.isSaving = false;
            		if(headers("X-cpmApp-alert") == 'cpmApp.systemConfig.updated'){
            			$state.go(vm.previousState);
            		}
	        	},
	        	function(error){
	        		vm.isSaving = false;
	        	}
            );
        }
    }
})();
