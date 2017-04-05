(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ContractInfoImportController', ContractInfoImportController);

    ContractInfoImportController.$inject = ['$state','$rootScope', '$scope','ContractInfo','Upload','previousState','AlertService'];

    function ContractInfoImportController ($state,$rootScope, $scope, ContractInfo, Upload,previousState,AlertService) {
        var vm = this;

        vm.previousState = previousState.name;
        vm.upload = upload;
        
        function upload () {
        	vm.isSaving = true;
        	var file = document.querySelector('input[type=file]').files;
        	if(file == undefined || file.length < 1){
        		vm.isSaving = false;
        		AlertService.error("cpmApp.contractInfo.upload.nonFile");
        	}else if(file.length>1){
        		vm.isSaving = false;
        		AlertService.error("cpmApp.contractInfo.upload.numFile");
        	}else{
        		Upload.upload({
            		method:'POST',
            		url:"api/contract-infos/uploadExcel",
            		data:{"file":file[0]},
            		headers: {'Content-Type':undefined},
            		transformRequest: angular.identity
            	}).success(function(data,a,b,c){
            		vm.isSaving = false;
            		if(data.msgKey && !data.success){
            			var param = {};
            			if(data.msgParam){
            				var obj = data.msgParam.split(",");
            				param.sheetNum = obj[0];
            				param.rowNum = obj[1];
            				param.columnNum = obj[2];
            			}
            			AlertService.error(data.msgKey,param);
            		}else if(data.msgKey){
            			AlertService.success(data.msgKey);
            			$state.go(vm.previousState);
            		}
            	}).error(function(data){
            		vm.isSaving = false;
            		if(data.msgKey && !data.success){
            			var param = {};
            			if(data.msgParam){
            				var obj = data.msgParam.split(",");
            				param.sheetNum = obj[0];
            				param.rowNum = obj[1];
            				param.columnNum = obj[2];
            			}
            			AlertService.error(data.msgKey,param);
            		}else if(data.msgKey){
            			AlertService.success(data.msgKey);
            			$state.go(vm.previousState);
            		}
            	});
        	}
        }
        vm.downloadTpl = downloadTpl;
        function downloadTpl(){
        	var url = "api/download-file/downloadXlsxTpl";
			
        	url += "?filePath="+encodeURI("importTpl/contractInfoImportTpl.xlsx");
        	
        	window.open(url);
        }
    }
})();
