(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('HolidayInfoDialogController', HolidayInfoDialogController);

    HolidayInfoDialogController.$inject = ['$timeout', '$scope', '$stateParams', 'entity', 'HolidayInfo','$state','DateUtils'];

//    function HolidayInfoDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, HolidayInfo,$state) {
    function HolidayInfoDialogController ($timeout, $scope, $stateParams,entity, HolidayInfo,$state,DateUtils) {
        var vm = this;

        vm.holidayInfo = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        
        console.log("------"+entity.type);
        //新增页下拉假日类型参数
        vm.holiStatuss = [{key:1,val:'正常工作日'},{key:2,val:'正常休息日'},{key:3,val:'年假'},{key:4,val:'国家假日'}];
        for(var i=0;i<vm.holiStatuss.length;i++){
        	if(entity.type == vm.holiStatuss[i].key){
        		vm.holidayInfo.typeName = vm.holiStatuss[i];
        		break;
        	}
        }
        
        if(entity && entity.currDay){
        	var str = String(entity.currDay);
        	vm.holidayInfo.currDay = new Date(str.substring(0,4),parseInt(str.substring(4,6))-1,str.substring(6,8));
        }

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
//            $uibModalInstance.dismiss('cancel');
        	$state.go('holiday-info', null, { reload: 'holiday-info' });
        }

        function save () {
        	var addHoliTypeName = vm.holidayInfo.typeName;
    		console.log("vm.addHoliType.key:"+addHoliTypeName.key+","+"vm.addHoliType.val:"+addHoliTypeName.val);
    		vm.holidayInfo.type=addHoliTypeName.key;
    		console.log("vm.holidayInfo.type:"+vm.holidayInfo.type);
    		vm.holidayInfo.currDay = DateUtils.convertLocalDateToFormat(vm.holidayInfo.currDay,"yyyyMMdd");
    		console.log("vm.holidayInfo.currDay:"+vm.holidayInfo.currDay);
//        	changeType();
        	console.log("vm.holidayInfo.type:"+vm.holidayInfo.type);
            vm.isSaving = true;
//            if (vm.holidayInfo.id !== null) {
                HolidayInfo.update(vm.holidayInfo, onSaveSuccess, onSaveError);
//            } else {
//                HolidayInfo.save(vm.holidayInfo, onSaveSuccess, onSaveError);
//            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('cpmApp:holidayInfoUpdate', result);
//            $uibModalInstance.close(result);
            $state.go('holiday-info');
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.createTime = false;
        vm.datePickerOpenStatus.updateTime = false;
        vm.datePickerOpenStatus.currDay = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
        
//        function changeType(){
//            		var addHoliType = vm.holidayInfo.type;
//            		console.log("vm.addHoliType.key:"+addHoliType.key+","+"vm.addHoliType.val:"+addHoliType.val);
//            		vm.holidayInfo.type=addHoliType.key;
//            		console.log("vm.holidayInfo.type:"+vm.holidayInfo.type);
//        }
    }
})();
