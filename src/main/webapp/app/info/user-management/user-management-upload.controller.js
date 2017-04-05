(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('UserManagementUploadController', UserManagementUploadController);

    UserManagementUploadController.$inject = ['$state','$rootScope', '$scope','Upload','previousState','AlertService','$http','$timeout','CpmUtil','$translate'];

    function UserManagementUploadController ($state,$rootScope, $scope, Upload,previousState,AlertService,$http,$timeout,CpmUtil,$translate) {
        var vm = this;
        vm.previousState = previousState.name;

        vm.downloadTpl = downloadTpl;
        function downloadTpl(){
        	var url = "api/download-file/downloadXlsxTpl";
			
        	url += "?filePath="+encodeURI("importTpl/userImportTpl.xlsx");
        	
        	window.open(url);
        }
        //上传文件
        $scope.handlingUrl = "api/users/uploadExcel";//处理文件的URL
        $scope.type = '1';//上传文件类型
        $scope.handleUploadFile = handleUploadFile;
        //初始化上传的信息
        CpmUtil.uploadFile(Upload,$scope,$timeout,$http);
        function handleUploadFile(file,filePath){
        	file.handleMsg = "数据处理中";
        	$http({
        		method:'GET',
        		url:$scope.handlingUrl,
        		params:{"filePath":filePath},
        		headers: {'Content-Type':undefined}
        	}).success(function(data,a,b,c){
        		file.handling = false;
        		vm.isSaving = false;
        		if(data.msgKey && !data.success){
        			var param = {};
        			if(data.msgParam){
        				var obj = data.msgParam.split(",");
        				param.sheetNum = obj[0];
        				param.rowNum = obj[1];
        				param.columnNum = obj[2];
        			}
        			file.handleMsg = $translate.instant(data.msgKey, param);
        			file.handleFail = true;
        		}else if(data.msgKey){
        			file.handleMsg = $translate.instant(data.msgKey);
        			file.handleFail = false;
        		}
        	}).error(function(data){
        		file.handling = false;
        		vm.isSaving = false;
        		if(data.msgKey && !data.success){
        			var param = {};
        			if(data.msgParam){
        				var obj = data.msgParam.split(",");
        				param.sheetNum = obj[0];
        				param.rowNum = obj[1];
        				param.columnNum = obj[2];
        			}
        			file.handleMsg = $translate.instant(data.msgKey, param);
        			file.handleFail = true;
        		}else if(data.msgKey){
        			file.handleMsg = $translate.instant(data.msgKey);
        			file.handleFail = false;
        		}
        	});
        }
    }
})();
