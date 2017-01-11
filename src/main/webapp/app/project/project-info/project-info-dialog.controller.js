(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ProjectInfoDialogController', ProjectInfoDialogController);

    ProjectInfoDialogController.$inject = ['$scope', '$rootScope','$state', '$stateParams', 'previousState', 'entity', 'ProjectInfo','AlertService','DateUtils'];

    function ProjectInfoDialogController ($scope, $rootScope,$state, $stateParams, previousState, entity, ProjectInfo,AlertService,DateUtils) {
        var vm = this;

        vm.projectInfo = entity;
        vm.previousState = previousState.name;
        vm.queryDept = previousState.queryDept;
        vm.contractChanged = contractChanged;
        vm.budgetChanged = budgetChanged;
        
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        
        loadContract();
        function loadContract(){
        	ProjectInfo.queryUserContract({
        		
	        	},
	        	function(data, headers){
	        		vm.contractInfos = data;
	        		if(vm.contractInfos && vm.contractInfos.length > 0){
	        			var select = false;
	        			for(var i = 0; i < vm.contractInfos.length; i++){
	        				if(entity.contractId == vm.contractInfos[i].key){
	        					vm.projectInfo.contractId = vm.contractInfos[i];
	        					select = true;
	        				}
	        			}
	        			if(!select){
	        				vm.projectInfo.contractId = vm.contractInfos[0];
	        			}
	        			contractChanged();
	        		}
	        	},
	        	function(error){
	        		AlertService.error(error.data.message);
	        		vm.contractInfos = [];
	        	}
        	);
        }
        //加载合同后面的采购单
        function contractChanged(){
        	var contractId = '';
        	
        	if(vm.projectInfo && vm.projectInfo.contractId && vm.projectInfo.contractId.key){
        		contractId = vm.projectInfo.contractId.key;
        	}else if(vm.projectInfo && vm.projectInfo.contractId){
        		contractId = vm.projectInfo.contractId;
        	}else{
        		vm.contractBudgets = [];
        		return;
        	}
        	ProjectInfo.queryUserContractBudget({
        		contractId:contractId
	        	},
	    		function(data, headers){
	        		vm.contractBudgets = data;
	        		if(vm.contractBudgets && vm.contractBudgets.length > 0){
	        			var select = false;
	        			for(var i = 0; i < vm.contractBudgets.length; i++){
	        				if(entity.budgetId == vm.contractBudgets[i].key){
	        					vm.projectInfo.budgetId = vm.contractBudgets[i];
	        					select = true;
	        				}
	        			}
	        			if(!select){
	        				vm.projectInfo.budgetId = vm.contractBudgets[0];
	        			}
	        			budgetChanged();
	        		}
	        	},
	        	function(error){
	        		AlertService.error(error.data.message);
	        		vm.contractBudgets = [];
	        	}
	        );
        }
        function budgetChanged(){
        	if(vm.projectInfo.budgetId && vm.projectInfo.budgetId.key){
        		vm.projectInfo.budgetOriginal = vm.projectInfo.budgetId.p2;
        	}else{
        		vm.projectInfo.budgetOriginal = vm.projectInfo.budgetOriginal;
        	}
        }
        function save () {
            vm.isSaving = true;
            var projectInfo = {};
            projectInfo.id = vm.projectInfo.id;
            projectInfo.serialNum = vm.projectInfo.serialNum;
            projectInfo.name = vm.projectInfo.name;
            projectInfo.pmId = vm.projectInfo.pmId;
            projectInfo.pm = vm.projectInfo.pm;
            projectInfo.deptId = vm.projectInfo.deptId;
            projectInfo.dept = vm.projectInfo.dept;
            projectInfo.contractId = vm.projectInfo.contractId;	//可能是对象
            projectInfo.budgetId = vm.projectInfo.budgetId;		//可能是对象
            projectInfo.startDay = vm.projectInfo.startDay;		//是date
            projectInfo.endDay = vm.projectInfo.endDay;			//是date
            projectInfo.budgetTotal = vm.projectInfo.budgetTotal;
            //校验对象
            //预算和合同是否一致
            if(projectInfo.contractId && projectInfo.contractId.key){
            	projectInfo.contractId = projectInfo.contractId.key;
            }
            if(projectInfo.budgetId && projectInfo.budgetId.key){
            	if(projectInfo.budgetId.p1 == projectInfo.contractId){
            		projectInfo.budgetId = projectInfo.budgetId.key;
            	}else{
            		AlertService.error("cpmApp.projectInfo.save.budgetError");
            		vm.isSaving = false;
            		return;
            	}
            }
            ProjectInfo.update(projectInfo,
	    		function(data, headers){
            		vm.isSaving = false;
//            		AlertService.error(error.data.message);
            		$state.go(vm.previousState);
	        	},
	        	function(error){
	        		vm.isSaving = false;
//	        		AlertService.error(error.data.message);
	        	}
	        );            
        }
        vm.datePickerOpenStatus.startDay = false;
        vm.datePickerOpenStatus.endDay = false;
        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
        var unsubscribe = $rootScope.$on('cpmApp:deptInfoSelected', function(event, result) {
        	vm.projectInfo.pmId = result.objId;
        	vm.projectInfo.pm = result.name;
        	vm.projectInfo.deptId = result.parentId;
        	vm.projectInfo.dept = result.parentName;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
