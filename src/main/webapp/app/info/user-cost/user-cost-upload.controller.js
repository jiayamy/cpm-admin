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
        	console.log("1111:");
        	console.log(file);
        	if(file.length>1){
        		AlertService.error("选择的文件数量不能大于1！");
        	}else{
        		Upload.upload({
            		method:'POST',
            		url:"api/user-costs/uploadExcel",
//            		file:data,
//            		data:{"file":file[0],"file":file[1]},
            		data:{"file":file[0]},
//          		file: file,
            		headers: {'Content-Type':undefined},
            		transformRequest: angular.identity 
            	});
        	}
        	
        }

    }
})();
