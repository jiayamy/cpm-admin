(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ContractUserDialogController', ContractUserDialogController);

    ContractUserDialogController.$inject = ['$state','ContractInfo','DateUtils','$rootScope', '$scope', '$stateParams','entity', 'ContractUser','previousState','AlertService'];

    function ContractUserDialogController ($state,ContractInfo,DateUtils,$rootScope, $scope, $stateParams, entity, ContractUser,previousState,AlertService) {
        var vm = this;

        vm.contractUser = entity;
        //处理加盟日，离开日
      	vm.contractUser.joinDay = DateUtils.convertYYYYMMDDDayToDate(vm.contractUser.joinDay);
        vm.contractUser.leaveDay = DateUtils.convertYYYYMMDDDayToDate(vm.contractUser.leaveDay);
        
        vm.previousState = previousState.name;
        vm.queryDept = previousState.queryDept;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        
        loadContractInfos();
        vm.save = save;
        
        function loadContractInfos(){
        	ContractInfo.queryContractInfo(
        		{
        			
        		},
        		function(data, headers){
        			vm.contractInfos = data;
            		if(vm.contractInfos && vm.contractInfos.length > 0){
            			for(var i = 0; i < vm.contractInfos.length; i++){
            				if(entity.contractId == vm.contractInfos[i].key){
            					vm.contractUser.contractId = vm.contractInfos[i];
            					angular.element('select[ng-model="vm.contractUser.contractId"]').parent().find(".select2-chosen").html(vm.contractInfos[i].val);
            				}
            			}
            		}
        		},
        		function(error){
        			AlertService.error(error.data.message);
        			vm.contractInfos = [];
        		}
        	);
        }

        function save () {
            vm.isSaving = true;
            var contractUser = {};
            vm.isSaving = true;
            var contractUser = {};
            
            contractUser.id = vm.contractUser.id;
            contractUser.contractId = vm.contractUser.contractId && vm.contractUser.contractId.key ? vm.contractUser.contractId.key : ""; 
            contractUser.userId = vm.contractUser.userId;
            contractUser.userName = vm.contractUser.userName;
            contractUser.deptId = vm.contractUser.deptId;
            contractUser.dept = vm.contractUser.dept;
            contractUser.joinDay = DateUtils.convertLocalDateToFormat(vm.contractUser.joinDay,"yyyyMMdd");
            contractUser.leaveDay = DateUtils.convertLocalDateToFormat(vm.contractUser.leaveDay,"yyyyMMdd");
            if(!contractUser.contractId || !contractUser.userId || !contractUser.userName ||!contractUser.joinDay){
            	vm.isSaving = false;
            	AlertService.error("cpmApp.contractUser.save.paramNone");
            	return;
            }
            if(!contractUser.leaveDay && parseInt(contractUser.leaveDay) > parseInt(contractUser.joinDay)){
            	vm.isSaving = false;
            	AlertService.error("cpmApp.contractUser.save.dayError");
            }
            ContractUser.update(contractUser,
    	    		function(data, headers){
                		vm.isSaving = false;
                		if(headers("X-cpmApp-alert") == 'cpmApp.contractUser.updated'){
                			$state.go(vm.previousState);
                		}
    	        	},
    	        	function(error){
    	        		vm.isSaving = false;
    	        	}
    	        ); 
        }

        function onSaveSuccess (result) {
            $scope.$emit('cpmApp:contractUserUpdate', result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.joinDay = false;
        vm.datePickerOpenStatus.leaveDay = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
        
        var unsubscribe = $rootScope.$on('cpmApp:deptInfoSelected', function(event, result) {
        	vm.contractUser.userId = result.objId;
        	vm.contractUser.userName = result.name;
        	vm.contractUser.deptId = result.parentId;
        	vm.contractUser.dept = result.parentName;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
