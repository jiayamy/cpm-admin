(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('UserCostDialogController', UserCostDialogController);

    UserCostDialogController.$inject = ['$timeout','$state','$rootScope', '$scope','previousState', '$stateParams','entity', 'UserCost','AlertService','DateUtils','User'];

    function UserCostDialogController ($timeout,$state,$rootScope, $scope, previousState, $stateParams, entity, UserCost,AlertService,DateUtils,User) {
        var vm = this;

        vm.previousState = previousState.name;
        vm.userCost = entity;
        vm.queryDept = previousState.queryDept;
//        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        console.log("5555:"+vm.userCost.sal);
        
        if(entity && entity.userId){
        	UserCost.getSerialNumByuserId({id:entity.userId},function(data){
            	vm.serialNum = data.serialNum;
            },function(){vm.serialNum = "";});
//        	vm.serialNum = vm.userCost.userId;
        }

        vm.statuss = [{key:1,val:"可用"},{key:2,val:"删除"}];
        for(var i=0;i<vm.statuss.length;i++){
        	if(entity.status == vm.statuss[i].key){
        		vm.userCost.status = vm.statuss[i];
        		break;
        	}
        }
        
        if(entity && entity.costMonth){
        	vm.sdf = entity.costMonth+"";
        	vm.userCost.costMonth = new Date(vm.sdf.substring(0,4),vm.sdf.substring(4,6)-1);
        }
        
        if(entity && entity.id){
        	vm.userCost.externalCost = entity.sal + entity.socialSecurity + entity.fund;
        }
        

        function save () {
            vm.isSaving = true;
            var userCost = {};
            userCost.id = vm.userCost.id;
            userCost.userId = vm.userCost.userId;
            userCost.userName = vm.userCost.userName;
            userCost.costMonth = DateUtils.convertLocalDateToFormat(vm.userCost.costMonth,"yyyyMM");
            userCost.internalCost = vm.userCost.internalCost;
            userCost.externalCost = vm.userCost.externalCost;
//            userCost.status = vm.userCost.status && vm.userCost.status.key ? vm.userCost.status.key:"";
            userCost.sal = vm.userCost.sal;
            userCost.socialSecurity = vm.userCost.socialSecurity;
            userCost.fund = vm.userCost.fund;
            if(!userCost.userId ||!userCost.userName || !userCost.costMonth || 
            		!userCost.sal || !userCost.socialSecurity || !userCost.fund){
            	AlertService.error("cpmApp.userCost.save.requriedError");
            	return;
            }
            UserCost.update(userCost, onSaveSuccess, onSaveError);
        }

        function onSaveSuccess (data,headers) {
            vm.isSaving = false;
            if(headers("X-cpmApp-alert") == 'cpmApp.userCost.updated'){
    			$state.go(vm.previousState);
    		}
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.costMonth = false;
        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
        
        var unsubscribe = $rootScope.$on('cpmApp:deptInfoSelected', function(event, result) {
        	vm.userCost.userId = result.objId;
        	vm.userCost.userName = result.name;
        	getSerialNum(result.objId);
        });
        $scope.$on('$destroy', unsubscribe);
        
        vm.getSerialNum = getSerialNum;
        function getSerialNum(userId){
        	UserCost.getSerialNumByuserId({id:userId},
        			function(data){vm.serialNum = data.serialNum},null);
        }
        
        $scope.getExternalCost = function(){
        	var sal = 0;
        	var socialSecurity = 0;
        	var fund = 0;
        	if(isNaN(vm.userCost.sal) || vm.userCost.sal == undefined){
        		sal = 0;
        	}else{
        		sal = vm.userCost.sal;
        	}
        	if(isNaN(vm.userCost.socialSecurity) || vm.userCost.socialSecurity == undefined){
        		socialSecurity = 0;
        	}else{
        		socialSecurity = vm.userCost.socialSecurity;
        	}
        	if(isNaN(vm.userCost.fund) || vm.userCost.fund == undefined){
        		fund = 0;
        	}else{
        		fund = vm.userCost.fund;
        	}
        	return sal + socialSecurity + fund;
        }
        
        $scope.$watch($scope.getExternalCost,function(newVal,oldVal){
        	console.log("newVal---:"+newVal);
        	vm.userCost.externalCost = newVal;
        });
//        $scope.$watch("vm.userCost.sal",function(newVal,oldVal){
//        	console.log(newVal);
//        	if(newVal === oldVal){return;}
//        	if(newVal == undefined){
//        		newVal = 0;
////        		vm.userCost.sal = 0;
//        	}
//        	if(oldVal == undefined){
//        		oldVal = 0;
//        	}
//        	var tmp = vm.userCost.externalCost;
//        	vm.userCost.externalCost = tmp+(newVal-oldVal);
//        });
//        $scope.$watch("vm.userCost.socialSecurity",function(newVal,oldVal){
//        	console.log(newVal);
//        	if(newVal === oldVal){return;}
//        	if(newVal == undefined){
//        		newVal = 0;
////        		vm.userCost.socialSecurity = 0;
//        	}
//        	if(oldVal == undefined){
//        		oldVal = 0;
//        	}
//        	var tmp = vm.userCost.externalCost;
//        	vm.userCost.externalCost = tmp+(newVal-oldVal);
//        });
//        $scope.$watch("vm.userCost.fund",function(newVal,oldVal){
//        	console.log(newVal);
//        	if(newVal === oldVal){return;}
//        	if(newVal == undefined){
//        		newVal = 0;
////        		vm.userCost.fund = 0;
//        	}
//        	if(oldVal == undefined){
//        		oldVal = 0;
//        	}
//        	var tmp = vm.userCost.externalCost;
//        	vm.userCost.externalCost = tmp+(newVal-oldVal);
//        });
    }
})();
