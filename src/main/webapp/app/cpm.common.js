(function () {
    'use strict';

    angular
        .module('cpmApp')
        .provider('CpmUtil',function() {
        	this.$get = function(){
        		return {
        			uploadFile : function(Upload,$scope,$timeout,$http){
        	    		//上传文件组件配置
        	    		$scope.usingFlash = FileAPI && FileAPI.upload != null;
        	    		$scope.files = [];
        	    		$scope.invalidFiles = [];
        	    		$scope.chunkSize = 102400;//支持续传的一次传输多大，单位B
        	    		$scope.randomNum = getRandomNum(0,100);
        	    		$scope.fileUploadUrl="cpmservlet/uploadFile"//上传url
        	    		$scope.type = $scope.type || '1';//上传文件类型
        	    		$scope.isResumeSupported = Upload.isResumeSupported();//是否支持断点续传
        	    		//监控无效文件
        	    		$scope.$watch('invalidFiles', function (invalidFiles) {
        	    			if (invalidFiles != null && !angular.isArray(invalidFiles)) {
        	    				$timeout(function () {
        	    					$scope.invalidFiles = [invalidFiles];
        	    				});
        	    			}
        	    			$timeout(function () {
    	    					for (var i = 0; i < $scope.invalidFiles.length; i++) {
        	    					(function (f) {
        	    						if(f.$errorMessages){
        	    							if(f.$errorMessages.maxSize){
        	    								f.invalidMsg = "文件大小最大只能为" + f.$errorParam;
        	    							}else if(f.$errorMessages.minSize){
        	    								f.invalidMsg = "文件大小最小只能为" + f.$errorParam;
        	    							}else if(f.$errorMessages.pattern){
        	    								f.invalidMsg = "文件后缀只能为" + f.$errorParam;
        	    							}else if(f.$errorMessages.maxFiles){
        	    								f.invalidMsg = "文件数最多只能为" + f.$errorParam + "个";
        	    							}else{
        	    								f.invalidMsg = f.$errorMessages + " 需要 "+ f.$errorParam;
        	    							}
        	    						}
        	    					})($scope.invalidFiles[i]);
        	    				}
    	    		        });
        	    		});
        	    		//监控正常文件
        	    		$scope.$watch('files', function (files) {
        	    			if (files != null) {
        	    				if (!angular.isArray(files)) {
        	    					$timeout(function () {
        	    						$scope.files = files = [files];
        	    					});
        	    				}
        	    			}
        	    			
        	    		});
        	    		//全部上传
        	    		$scope.uploadAll = function(){
        	    			if($scope.files){
        	    				for (var i = 0; i < $scope.files.length; i++) {
        	    					$scope.errorMsg = null;
        	    					(function (f) {
        	    						if(f.progress == undefined){
        	    							$scope.upload(f, true);//每个文件都执行上传
        	    						}
        	    					})($scope.files[i]);
        	    				}
        	    			}
        	    		}
        	    		//重新上传
        	    		$scope.restart = function (file) {
        	    			if (Upload.isResumeSupported()) {//支持续传
        	    				if(file.currentdate == undefined){
        	    					file.currentdate = getNowFormatDate();
        	    				}
        	    				//首先清除后台已有的文件大小
        	    				var restartUrl = $scope.fileUploadUrl+'?restart=true&name=' + encodeURIComponent(file.name)
        	    				+'&uploadTime=' + file.currentdate
        	    				+'&mark=' + $scope.randomNum
        	    				+'&type=' + $scope.type;
        	    				$http.get(restartUrl).then(function () {
        	    					$scope.upload(file, true);
        	    				});
        	    			} else {
        	    				$scope.upload(file);
        	    			}
        	    		};
        	    		//上传文件
        	    		$scope.upload = function (file, resumable) {
        	    			$scope.errorMsg = null;
        	    			uploadUsingUpload(file, resumable);
        	    		};
        	    		function uploadUsingUpload(file, resumable) {
        	    			file.handling = false;
        	    			file.uploadFinished = false;
        	    			file.handleFail = false;
        	    			if(file.currentdate == undefined){
        	    				file.currentdate = getNowFormatDate();
        	    			}
        	    			var resumeSizeUrl = $scope.fileUploadUrl
        	    			+'?name=' + encodeURIComponent(file.name)
        	    			+'&uploadTime=' + file.currentdate
        	    			+'&mark=' + $scope.randomNum
        	    			+'&type=' + $scope.type;
        	    			file.upload = Upload.upload({
        	    				url: resumeSizeUrl,
        	    				resumeSizeUrl: resumeSizeUrl,
        	    				resumeChunkSize: resumable ? $scope.chunkSize : null,
        	    						headers: {
        	    							'optional-header': 'header-value',
        	    							'X-File-Upload-Time' : ''+ file.currentdate,
        	    							'X-File-Upload-Mark' : ''+$scope.randomNum,
        	    							'X-File-Upload-Name' : encodeURIComponent(file.name),
        	    							'X-File-Upload-Type' : $scope.type
        	    						},
        	    						data: {uploadFile: file}
        	    			});
        	    			file.upload.then(function (response) {
        	    				$timeout(function () {
        	    					file.result = response.data;
        	    					if(response.data.filePath && response.data.finished){
        	    						file.filePath = response.data.filePath;
        	    						file.handling = true;
        	    						file.uploadFinished = true;
        	    						if($scope.handleUploadFile){
        	    							$scope.handleUploadFile(file,response.data.filePath);
        	    						}
        	    					}
        	    				});
        	    			}, function (response) {
        	    				if (response.status > 0)
        	    					$scope.errorMsg = response.status + ': ' + response.data;
        	    			}, function (evt) {
        	    				file.progress = Math.min(100, parseInt(100.0 * evt.loaded / evt.total));
        	    				file.progreeStyle = {"width":file.progress+"%"};
        	    			});
        	    			file.upload.xhr(function (xhr) {
        	//            	xhr.upload.addEventListener('abort', function(){
        	//             	}, false);
        	    			});
        	    		}
        	    		//获取随机数
        	    		function getRandomNum(min,max)
        	    		{
        	    			var range = max - min;   
        	    			var rand = Math.random();   
        	    			return (min + Math.round(rand * range));  
        	    		};
        	    		//获取当前时间
        	    		function getNowFormatDate() {
        	    			var time = new Date();
        	    			var month = time.getMonth() + 1;
        	    			var date = time.getDate();
        	    			var hours = time.getHours();
        	    			var minutes = time.getMinutes();
        	    			var seconds = time.getSeconds();
        	    			var milliseconds = time.getMilliseconds();
        	    			if (month >= 1 && month <= 9) {
        	    				month = "0" + month;
        	    			}
        	    			if (date >= 0 && date <= 9) {
        	    				date = "0" + date;
        	    			}
        	    			if (hours >= 0 && hours <= 9) {
        	    				hours = "0" + hours;
        	    			}
        	    			if (minutes >= 0 && minutes <= 9) {
        	    				minutes = "0" + minutes;
        	    			}
        	    			if (seconds >= 0 && seconds <= 9) {
        	    				seconds = "0" + seconds;
        	    			}
        	    			if (milliseconds >= 0 && milliseconds <= 9) {
        	    				milliseconds = "00" + milliseconds;
        	    			}else if (milliseconds >= 10 && milliseconds <= 99) {
        	    				milliseconds = "0" + milliseconds;
        	    			}
        	    			
        	    			var currentdate = time.getFullYear() + month + date
        	    			+ hours + minutes + seconds + milliseconds;
        	    			return currentdate;
        	    		};
        	    		return $scope;
        	    	}
        		}
        	}
        });
})();
