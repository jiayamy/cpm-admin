(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ProjectUserDialogController', ProjectUserDialogController);

    ProjectUserDialogController.$inject = ['$timeout', '$scope', '$rootScope','$state','$stateParams','previousState', 'entity', 'ProjectUser','ProjectInfo','DateUtils','AlertService','OutsourcingUser'];

    function ProjectUserDialogController ($timeout, $scope, $rootScope,$state,$stateParams,previousState, entity, ProjectUser,ProjectInfo,DateUtils,AlertService,OutsourcingUser) {
        var vm = this;
        
        vm.projectUser = entity;
        var contractId = entity.contractId;
        
        //关联合同的类型，添加人员等级
        if (vm.projectUser.type == 2) {
			vm.isOutsourcing = true;
			loadRanks(contractId);
		}
        
        //加载rank的下拉框
		function loadRanks(contractId){
        	OutsourcingUser.queryRank(
        		{
        			contractId:contractId
        		},
	    		function(data, headers){
	    			vm.ranks = data;
	        		if(vm.ranks && vm.ranks.length > 0){
	        			for(var i = 0; i < vm.ranks.length; i++){
	        				if(entity.rank == vm.ranks[i].val){
	        					vm.projectUser.rank = vm.ranks[i];
	        				}
	        			}
	        		}
	    		},
	    		function(error){
	    			vm.ranks = [];
	    		}
        	);
		}
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
        //新增页面改变项目，加载人员等级下拉框
        vm.changeProject = changeProject;
        function changeProject(){
        	if (vm.projectUser.projectId.type == 2) {
				vm.isOutsourcing = true;
				vm.isRank = true;
				loadRanks(vm.projectUser.projectId.p1);
			}else {
				vm.isOutsourcing = false;
			}
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
            if (vm.projectUser.type == 2 || vm.projectUser.projectId.type == 2) {
				projectUser.rank = vm.projectUser.rank && vm.projectUser.rank.key ? vm.projectUser.rank.val : "";
				vm.isRank = true;
			}
            if(!projectUser.projectId || !projectUser.userId || !projectUser.userName || !projectUser.userRole || !projectUser.joinDay){
            	vm.isSaving = false;
            	AlertService.error("cpmApp.projectUser.save.paramNone");
            	return;
            }
            if(!projectUser.leaveDay && parseInt(projectUser.leaveDay) > parseInt(projectUser.joinDay)){
            	vm.isSaving = false;
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
