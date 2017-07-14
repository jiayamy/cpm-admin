(function(){
	'use strict';
	
	angular
		.module('cpmApp')
		.controller('UserProjectInputController',UserProjectInputController);
	
	UserProjectInputController.$inject = ['$scope','$rootScope', '$state', 'UserProjectInput', 'ProjectInfo', 'ParseLinks', 'AlertService', 'paginationConstants', 'pagingParams','DateUtils'];
	
	function UserProjectInputController($scope,$rootScope, $state, UserProjectInput, ProjectInfo, ParseLinks, AlertService, paginationConstants, pagingParams,DateUtils){
		var vm = this;
        vm.transition = transition;
        vm.clear = clear;
        vm.search = search;
        vm.exportXls = exportXls;
        vm.searchQuery = {};
        var today = DateUtils.convertLocalDateToFormat(new Date(),"yyyyMMdd");
        if(pagingParams.startTime == undefined){//默认本月初
        	//上个月第一天
        	pagingParams.startTime = DateUtils.convertLocalDateToFormat(new Date(today.substring(0,4),parseInt(today.substring(4,6))-2,1),"yyyyMMdd");
        }
        if(pagingParams.endTime == undefined){//默认当天
        	pagingParams.endTime = DateUtils.convertLocalDateToFormat(new Date(new Date(today.substring(0,4),parseInt(today.substring(4,6))-1,1).getTime() - 1000*60*60*24),"yyyyMMdd");
        }
        //搜索项中的参数
        vm.searchQuery.startTime= DateUtils.convertYYYYMMDDDayToDate(pagingParams.startTime);
        vm.searchQuery.endTime= DateUtils.convertYYYYMMDDDayToDate(pagingParams.endTime);
        
        vm.userProjectInputs = [];
        
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        
        if (!vm.searchQuery.startTime && !vm.searchQuery.endTime
        		&& !vm.searchQuery.userId && !vm.searchQuery.showTotal && !vm.searchQuery.projectId){
        	vm.haveSearch = null;
        }else{
        	vm.haveSearch = true;
        }
        
        //加载搜索下拉框
        vm.projectInfos = [];
        loadProjectInfos();
        function loadProjectInfos(){
        	ProjectInfo.queryProjectInfo(
        		{
        			
        		},
        		function(data, headers){
        			vm.projectInfos = data;
        		},
        		function(error){
        			AlertService.error(error.data.message);
        		}
        	);
        }
        
        loadAll();
        function loadAll(){
        	UserProjectInput.query({
        		startTime : pagingParams.startTime,
        		endTime : pagingParams.endTime,
        		userId : pagingParams.userId,
        		projectId : pagingParams.projectId,
        		showTotal : pagingParams.showTotal
        	},onSuccess,onError);
        	function onSuccess(data, headers) {
                vm.userProjectInputs = handleData(data);
            }
            function onError(error) {
                AlertService.error(error.data.message);
            }
            function handleData(data){
            	return data;
            }
        }
        
        vm.datePickerOpenStatus.startTime = false;
        vm.datePickerOpenStatus.endTime = false;
        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
        
        function search() {
            if (!vm.searchQuery.startTime && !vm.searchQuery.endTime
            		&& !vm.searchQuery.userId && !vm.searchQuery.showTotal && !vm.searchQuery.projectId){
                return vm.clear();
            }
            vm.haveSearch = true;
            vm.transition();
        }
        
        function clear() {
            vm.searchQuery = {};
            var today = DateUtils.convertLocalDateToFormat(new Date(),"yyyyMMdd");
            vm.searchQuery.startTime = new Date(today.substring(0,4),parseInt(today.substring(4,6))-2,1);
            vm.searchQuery.endTime = new Date(new Date(today.substring(0,4),parseInt(today.substring(4,6))-1,1).getTime() - 1000*60*60*24);
            
            vm.searchQuery.showTotal = false;
            vm.searchQuery.projectId = [];
            vm.haveSearch = true;
            //多选的时候需要清理
            angular.element('select[ng-model="vm.searchQuery.projectId"]').parent().find('ul[class="select2-choices"]').children().remove(".select2-search-choice");
            
            vm.transition();
        }
        
        function transition() {
            pagingParams.startTime = DateUtils.convertLocalDateToFormat(vm.searchQuery.startTime,"yyyyMMdd");
	    	pagingParams.endTime = DateUtils.convertLocalDateToFormat(vm.searchQuery.endTime,"yyyyMMdd");
	    	pagingParams.userId = vm.searchQuery.userId;
	        pagingParams.userName = vm.searchQuery.userName;
	        pagingParams.showTotal = vm.searchQuery.showTotal;
	        var projectId = '';
	        if(vm.searchQuery.projectId && vm.searchQuery.projectId.length > 0){
	        	var l = vm.searchQuery.projectId.length;
	        	for(var i = 0; i < l ; i++){
	        		projectId += vm.searchQuery.projectId[i].key;
	        		if( i < (l - 1)){
	        			projectId += ',';
	        		}
	        	}
	        }
	        pagingParams.projectId = projectId;
            loadAll();
        }
        
        var unsubscribe = $rootScope.$on('cpmApp:deptInfoSelected', function(event, result) {
        	if(result.length > 0){
        		vm.searchQuery.userId = [];
        		for(var i = 0;i<result.length;i++){
        			vm.searchQuery.userId.push(result[i].objId);
        		}
        		if (result.length > 1) {
					vm.searchQuery.userName = "已选" + result.length + "个员工";
				}else{
					vm.searchQuery.userName = result[0].name;
				}
        	}else{
        		vm.searchQuery.userId = result.objId;
            	vm.searchQuery.userName = result.name;
        	}
        });
        $scope.$on('$destroy', unsubscribe);
        
        vm.show = show;
        function show(){
        	if(vm.searchQuery.showTotal == true){
        		vm.searchQuery.showTotal = false;
        	}else{
        		vm.searchQuery.showTotal = true;
        	}
        }
        
        function exportXls(){//导出Xls
        	var url = "api/user-project-input/exportXls";
        	var c = 0;
        	var startTime = DateUtils.convertLocalDateToFormat(vm.searchQuery.startTime,"yyyyMMdd");
        	var endTime = DateUtils.convertLocalDateToFormat(vm.searchQuery.endTime,"yyyyMMdd");
        	var userId = vm.searchQuery.userId;
        	var showTotal = vm.searchQuery.showTotal;
        	var projectId = '';
	        if(vm.searchQuery.projectId && vm.searchQuery.projectId.length > 0){
	        	var l = vm.searchQuery.projectId.length;
	        	for(var i = 0; i < l ; i++){
	        		projectId += vm.searchQuery.projectId[i].key;
	        		if( i < (l - 1)){
	        			projectId += ',';
	        		}
	        	}
	        }
			if(startTime){
				if(c == 0){
					c++;
					url += "?";
				}else{
					url += "&";
				}
				url += "startTime="+encodeURI(startTime);
			}
			if(endTime){
				if(c == 0){
					c++;
					url += "?";
				}else{
					url += "&";
				}
				url += "endTime="+encodeURI(endTime);
			}
			if(userId){
				if(c == 0){
					c++;
					url += "?";
				}else{
					url += "&";
				}
				url += "userId="+encodeURI(userId);
			}
			if(projectId){
				if(c == 0){
					c++;
					url += "?";
				}else{
					url += "&";
				}
				url += "projectId="+encodeURI(projectId);
			}
			if(showTotal){
				if(c == 0){
					c++;
					url += "?";
				}else{
					url += "&";
				}
				url += "showTotal="+encodeURI(showTotal);
			}
        	window.open(url);
        }
	}
})();