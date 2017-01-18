(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('UserCostDialogController', UserCostDialogController);

    UserCostDialogController.$inject = ['$timeout','$state', '$scope', '$stateParams','entity', 'UserCost','AlertService','DateUtils'];

    function UserCostDialogController ($timeout,$state, $scope, $stateParams, entity, UserCost,AlertService,DateUtils) {
        var vm = this;

        vm.userCost = entity;
        vm.clear = clear;
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
        
        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
//            $uibModalInstance.dismiss('cancel');
        	$state.go('user-cost', null, { reload: 'user-cost' });
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
            	AlertService.error("cpmApp.userCost.save.paramNone");
            	return;
            }
            UserCost.update(userCost, onSaveSuccess, onSaveError);
//            if (vm.userCost.id !== null) {
//                UserCost.update(vm.userCost, onSaveSuccess, onSaveError);
//            } else {
//                UserCost.save(vm.userCost, onSaveSuccess, onSaveError);
//            }
        }

        function onSaveSuccess (result) {
//            $scope.$emit('cpmApp:userCostUpdate', result);
//            $uibModalInstance.close(result);
        	$state.go('user-cost');
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

//        vm.datePickerOpenStatus.createTime = false;
//        vm.datePickerOpenStatus.updateTime = false;
        vm.datePickerOpenStatus.costMonth = false;
        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
