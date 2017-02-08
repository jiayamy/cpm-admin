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
        vm.accAdd = accAdd;
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
        	vm.userCost.externalCost = entity.sal + entity.socialSecurityFund + entity.otherExpense;
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
            userCost.socialSecurityFund = vm.userCost.socialSecurityFund;
            userCost.otherExpense = vm.userCost.otherExpense;
            if(!userCost.userId ||!userCost.userName || !userCost.costMonth || 
            		!userCost.sal || !userCost.socialSecurityFund || !userCost.otherExpense){
            	AlertService.error("cpmApp.userCost.save.requriedError");
            	return;
            }
            UserCost.update(userCost, onSaveSuccess, onSaveError);
        }

        function onSaveSuccess (data,headers) {
            vm.isSaving = false;
            if(headers("X-cpmApp-alert") == 'cpmApp.userCost.updated'){
    			$state.go(vm.previousState,{},{reload:true});
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
        	var socialSecurityFund = 0;
        	var otherExpense = 0;
        	if(isNaN(vm.userCost.sal) || vm.userCost.sal == undefined){
        		sal = 0;
        	}else{
        		sal = vm.userCost.sal;
        	}
        	if(isNaN(vm.userCost.socialSecurityFund) || vm.userCost.socialSecurityFund == undefined){
        		socialSecurityFund = 0;
        	}else{
        		socialSecurityFund = vm.userCost.socialSecurityFund;
        	}
        	if(isNaN(vm.userCost.otherExpense) || vm.userCost.otherExpense == undefined){
        		otherExpense = 0;
        	}else{
        		otherExpense = vm.userCost.otherExpense;
        	}
        	var sum = accAdd(sal,socialSecurityFund);
        	sum = accAdd(sum,otherExpense);
        	return sum;
//        	return sal + socialSecurityFund + otherExpense;
        }
        
        $scope.$watch($scope.getExternalCost,function(newVal,oldVal){
        	console.log("newVal---:"+newVal);
        	vm.userCost.externalCost = newVal;
        });
        
        // 两个浮点数求和  
        function accAdd(num1,num2){  
           var r1,r2,m;  
           try{  
               r1 = num1.toString().split('.')[1].length;  
           }catch(e){  
               r1 = 0;  
           }  
           try{  
               r2=num2.toString().split(".")[1].length;  
           }catch(e){  
               r2=0;  
           }  
           m=Math.pow(10,Math.max(r1,r2));  
           return Math.round(num1*m+num2*m)/m;  
        }
    }
})();
