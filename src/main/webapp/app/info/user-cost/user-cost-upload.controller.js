(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('UserCostUploadController', UserCostUploadController);

    UserCostUploadController.$inject = ['$state','$rootScope', '$scope','UserCost','Upload','previousState','AlertService'];

    function UserCostUploadController ($state,$rootScope, $scope, UserCost, Upload,previousState,AlertService) {
        var vm = this;

        vm.previousState = previousState.name;
        vm.upload = upload;
        
        function upload () {
        	vm.isSaving = true;
        	var file = document.querySelector('input[type=file]').files;
        	console.log(file);
        	if(file == undefined || file.length < 1){
        		AlertService.error("cpmApp.userCost.upload.nonFile");
        	}else if(file.length>1){
        		AlertService.error("cpmApp.userCost.upload.numFile");
        	}else{
        		Upload.upload({
            		method:'POST',
            		url:"api/user-costs/uploadExcel",
            		data:{"file":file[0]},
            		headers: {'Content-Type':undefined},
            		transformRequest: angular.identity
            	}).success(function(data){
            		$state.go(vm.previousState);
            	});
        	}
        	
        }

    }
})();
