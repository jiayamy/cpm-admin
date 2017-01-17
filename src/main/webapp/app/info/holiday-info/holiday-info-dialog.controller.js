(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('HolidayInfoDialogController', HolidayInfoDialogController);

    HolidayInfoDialogController.$inject = ['$timeout', '$scope', '$stateParams', 'entity', 'HolidayInfo','$state','DateUtils'];

    function HolidayInfoDialogController ($timeout, $scope, $stateParams,entity, HolidayInfo,$state,DateUtils) {
        var vm = this;

        vm.holidayInfo = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        
        //新增页下拉假日类型参数
        vm.holiStatuss = [{key:1,val:'正常工作日'},{key:2,val:'正常休息日'},{key:3,val:'年假'},{key:4,val:'国家假日'}];
        for(var i=0;i<vm.holiStatuss.length;i++){
        	if(entity.type == vm.holiStatuss[i].key){
        		vm.holidayInfo.type = vm.holiStatuss[i];
        		break;
        	}
        }
        
        if(entity && entity.currDay){
        	vm.holidayInfo.currDay = DateUtils.convertDayToDate(entity.currDay);
        }

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
        	$state.go('holiday-info', null, { reload: 'holiday-info' });
        }

        function save () {
            vm.isSaving = true;
            var holidayInfo = {};
            holidayInfo.id = vm.holidayInfo.id;
            holidayInfo.type = vm.holidayInfo.type && vm.holidayInfo.type.key ?  vm.holidayInfo.type.key : "";
            holidayInfo.currDay = DateUtils.convertLocalDateToFormat(vm.holidayInfo.currDay,"yyyyMMdd");
            
            HolidayInfo.update(holidayInfo, onSaveSuccess, onSaveError);
        }

        function onSaveSuccess (result) {
            $state.go('holiday-info');
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.currDay = false;
        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
        
    }
})();
