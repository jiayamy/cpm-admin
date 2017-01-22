(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('UserCostDialogController', UserCostDialogController);

    UserCostDialogController.$inject = ['$timeout','$state','$rootScope', '$scope','previousState', '$stateParams','entity', 'UserCost','AlertService','DateUtils'];

    function UserCostDialogController ($timeout,$state,$rootScope, $scope, previousState, $stateParams, entity, UserCost,AlertService,DateUtils) {
        var vm = this;

        vm.previousState = previousState.name;
        vm.userCost = entity;
        vm.queryDept = previousState.queryDept;
//        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;

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
        

        function save () {
            vm.isSaving = true;
            var userCost = {};
            userCost.id = vm.userCost.id;
            userCost.userId = vm.userCost.userId;
            userCost.userName = vm.userCost.userName;
            userCost.costMonth = DateUtils.convertLocalDateToFormat(vm.userCost.costMonth,"yyyyMM");
            userCost.internalCost = vm.userCost.internalCost;
            userCost.externalCost = vm.userCost.externalCost;
            userCost.status = vm.userCost.status && vm.userCost.status.key ? vm.userCost.status.key:"";
            if(!userCost.userId ||!userCost.userName || !userCost.costMonth || !userCost.status){
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
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
