(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ProjectUserDialogController', ProjectUserDialogController);

    ProjectUserDialogController.$inject = ['$timeout', '$scope', '$rootScope','$state','$stateParams','previousState', 'entity', 'ProjectUser','ProjectInfo','DateUtils','AlertService'];

    function ProjectUserDialogController ($timeout, $scope, $rootScope,$state,$stateParams,previousState, entity, ProjectUser,ProjectInfo,DateUtils,AlertService) {
        var vm = this;

        vm.projectUser = entity;
        //处理加盟日，离开日
      	vm.projectUser.joinDay = DateUtils.convertYYYYMMDDDayToDate(vm.projectUser.joinDay);
        vm.projectUser.leaveDay = DateUtils.convertYYYYMMDDDayToDate(vm.projectUser.leaveDay);
        
        vm.previousState = previousState.name;
        vm.queryDept = previousState.queryDept;
        
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        
        loadProjectInfos();
        function loadProjectInfos(){
        	ProjectInfo.queryProjectInfo(
        		{
        			
        		},
        		function(data, headers){
        			vm.projectInfos = data;
            		if(vm.projectInfos && vm.projectInfos.length > 0){
            			for(var i = 0; i < vm.projectInfos.length; i++){
            				if(entity.projectId == vm.projectInfos[i].key){
            					vm.projectUser.projectId = vm.projectInfos[i];
            				}
            			}
            		}
        		},
        		function(error){
        			AlertService.error(error.data.message);
        			vm.projectInfos = [];
        		}
        	);
        }
        
        vm.save = save;
        function save () {
            vm.isSaving = true;
            var projectUser = {};
            
            projectUser.id = vm.projectUser.id;
            projectUser.projectId = vm.projectUser.projectId && vm.projectUser.projectId.key ? vm.projectUser.projectId.key : ""; 
            projectUser.userId = vm.projectUser.userId;
            projectUser.userName = vm.projectUser.userName;
            projectUser.userRole = vm.projectUser.userRole;
            projectUser.joinDay = DateUtils.convertLocalDateToFormat(vm.projectUser.joinDay,"yyyyMMdd");
            projectUser.leaveDay = DateUtils.convertLocalDateToFormat(vm.projectUser.leaveDay,"yyyyMMdd");
            if(!projectUser.projectId || !projectUser.userId || !projectUser.userName || !projectUser.userRole || !projectUser.joinDay){
            	AlertService.error("cpmApp.projectUser.save.paramNone");
            	return;
            }
            if(!projectUser.leaveDay && parseInt(projectUser.leaveDay) > parseInt(projectUser.joinDay)){
            	AlertService.error("cpmApp.projectUser.save.dayError");
            }
            ProjectUser.update(projectUser,
    	    		function(data, headers){
                		vm.isSaving = false;
                		if(headers("X-cpmApp-alert") == 'cpmApp.projectUser.updated'){
                			$state.go(vm.previousState);
                		}
    	        	},
    	        	function(error){
    	        		vm.isSaving = false;
    	        	}
    	        ); 
        }

        vm.datePickerOpenStatus.joinDay = false;
        vm.datePickerOpenStatus.leaveDay = false;
        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
        
        var unsubscribe = $rootScope.$on('cpmApp:deptInfoSelected', function(event, result) {
        	vm.projectUser.userId = result.objId;
        	vm.projectUser.userName = result.name;
        });
        $scope.$on('$destroy', unsubscribe);
        
    }
})();
