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
        
//        $timeout(function (){
//            angular.element('.form-group:eq(1)>input').focus();
//        });

//        function clear () {
//            $uibModalInstance.dismiss('cancel');
//        	$state.go('user-cost', null, { reload: 'user-cost' });
//        }

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
//            if (vm.userCost.id !== null) {
//                UserCost.update(vm.userCost, onSaveSuccess, onSaveError);
//            } else {
//                UserCost.save(vm.userCost, onSaveSuccess, onSaveError);
//            }
        }

//        function onSaveSuccess (result) {
        function onSaveSuccess (data,headers) {
//            $scope.$emit('cpmApp:userCostUpdate', result);
//            $uibModalInstance.close(result);
//        	$state.go('user-cost');
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
//        	vm.userCost.userNameId = result.objId;
        	vm.userCost.userName = result.name;
//        	vm.userCost.deptId = result.parentId;
//        	vm.userCost.dept = result.parentName;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
