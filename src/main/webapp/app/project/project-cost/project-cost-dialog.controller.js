(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ProjectCostDialogController', ProjectCostDialogController);

    ProjectCostDialogController.$inject = ['$timeout', '$scope','$state', '$stateParams', 'previousState', 'entity', 'ProjectCost','ProjectInfo','AlertService','DateUtils'];

    function ProjectCostDialogController ($timeout, $scope,$state, $stateParams, previousState, entity, ProjectCost,ProjectInfo,AlertService,DateUtils) {
        var vm = this;

        vm.previousState = previousState.name;
        vm.projectCost = entity;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        //处理类型
        if(entity.id == null){
        	vm.types = [{key:2,val:'差旅'},{key:3,val:'采购'},{key:4,val:'商务'}];
        }else{
        	vm.types = [{key:1,val:'工时'},{key:2,val:'差旅'},{key:3,val:'采购'},{key:4,val:'商务'}];
        	for(var j = 0; j < vm.types.length ; j++){
        		if(entity.type == vm.types[j].key){
					vm.projectCost.type = vm.types[j];
				}
        	}
        }
        //处理costDay
        vm.projectCost.costDay = DateUtils.convertYYYYMMDDDayToDate(vm.projectCost.costDay);
        
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
            					vm.projectCost.projectId = vm.projectInfos[i];
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
        
        function save () {
            vm.isSaving = true;
           	var projectCost = {};
           	projectCost.id = vm.projectCost.id;
           	projectCost.projectId = vm.projectCost.projectId && vm.projectCost.projectId.key ? vm.projectCost.projectId.key : "";
           	projectCost.name = vm.projectCost.name;
           	projectCost.type = vm.projectCost.type && vm.projectCost.type.key ? vm.projectCost.type.key : "";
           	projectCost.costDay = DateUtils.convertLocalDateToFormat(vm.projectCost.costDay,"yyyyMMdd");
           	projectCost.total = vm.projectCost.total;
           	projectCost.costDesc = vm.projectCost.costDesc;
           	if(!projectCost.projectId || !projectCost.name || !projectCost.type || !projectCost.costDay || projectCost.total == undefined ){
           		vm.isSaving = false;
            	AlertService.error("cpmApp.projectCost.save.paramNone");
            	return;
            }
            ProjectCost.update(projectCost, 
        		function(data, headers){
            		vm.isSaving = false;
            		if(headers("X-cpmApp-alert") == 'cpmApp.projectCost.updated'){
            			$state.go(vm.previousState);
            		}
	        	},
	        	function(error){
	        		vm.isSaving = false;
	        	}
            );
        }

        vm.datePickerOpenStatus.costDay = false;
        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
