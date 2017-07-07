(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('HolidayInfoCalendarController', HolidayInfoCalendarController);

    HolidayInfoCalendarController.$inject = ['$timeout', '$scope', '$stateParams', 'entity', 'HolidayInfo','$state','DateUtils','previousState'];

    function HolidayInfoCalendarController ($timeout, $scope, $stateParams,entity, HolidayInfo,$state,DateUtils,previousState) {
        var vm = this;
        vm.previousState = previousState.name;
        
        vm.searchQuery = {};
        if(entity.currDay){
        	vm.searchQuery.currDay = DateUtils.convertDayToDate(entity.currDay);
        }
        if(vm.searchQuery.currDay == undefined){
        	vm.searchQuery.currDay = new Date();
        }
        /**
         * 往前加一个月时间
         */
        vm.arrowLeft = arrowLeft;
        function arrowLeft(){
        	if(vm.searchQuery.currDay == undefined || vm.searchQuery.currDay == ''){
            	vm.searchQuery.currDay = new Date();
            }
        	var date = DateUtils.convertLocalDateToFormat(vm.searchQuery.currDay,"yyyyMM");
        	vm.searchQuery.currDay = new Date(date.substring(0,4),parseInt(date.substring(4,6))-1-1,1);
        	vm.search();
        }
        /**
         * 往后加一个月时间
         */
        vm.arrowRight = arrowRight;
        function arrowRight(){
        	if(vm.searchQuery.currDay == undefined || vm.searchQuery.currDay == ''){
            	vm.searchQuery.currDay = new Date();
            }
        	var date = DateUtils.convertLocalDateToFormat(vm.searchQuery.currDay,"yyyyMM");
        	vm.searchQuery.currDay = new Date(date.substring(0,4),parseInt(date.substring(4,6))-1+1,1);
        	vm.search();
        }
        
        vm.search = search;
        function search(){
        	loadAll();
        }
        loadAll();
        function loadAll () {
        	var currDay = "";
        	if(vm.searchQuery.currDay){
        		currDay = DateUtils.convertLocalDateToFormat(vm.searchQuery.currDay,"yyyyMMdd");
        	}
        	HolidayInfo.queryCalendar({
        		currDay: currDay
            }, onSuccess, onError);
            function onSuccess(data, headers) {
            	vm.year = data.year;
            	vm.month = data.month;
            	vm.weeks = data.weeks;
            }
            function onError(error) {
            }
        }
        //下拉假日类型参数
        vm.holidayTypes = [{key:1,val:'正常工作日'},{key:2,val:'正常休息日'},{key:3,val:'年假'},{key:4,val:'国假假日'}];

        vm.clear = clear;
        function clear () {
        	$state.go('holiday-info', null, { reload: 'holiday-info' });
        }
        vm.datePickerOpenStatus = {};
        vm.datePickerOpenStatus.currDay = false;
        
        vm.openCalendar = openCalendar;
        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
        //获取日期边标识的css样式
        vm.getCalendarDayClass = getCalendarDayClass;
        function getCalendarDayClass(day){
        	if(day.type == null || day.type == undefined){
        		return '';
        	}
        	var className = 'wd-calendar-type-workday';
        	if(day.type == 1 && (day.dayOfWeek == 1 || day.dayOfWeek == 7)){
        		name = 'wd-calendar-type-onDuty';
        	}else if(day.type == 1){
        		name = 'wd-calendar-type-workday';
        	}else if(day.type == 2){
        		name = 'wd-calendar-type-weekend';
        	}else if(day.type == 3){
        		name = 'wd-calendar-type-annualLeave';
        	}else if(day.type == 4){
        		name = 'wd-calendar-type-holiday';
        	}
        	return name;
        }
        //获取日期边的标识
        vm.getCalendarDayMark = getCalendarDayMark;
        function getCalendarDayMark(day){
        	if(day.type == null || day.type == undefined){
        		return '';
        	}
        	var name = '工作';
        	if(day.type == 1 && (day.dayOfWeek == 1 || day.dayOfWeek == 7)){
        		name = '上班';
        	}else if(day.type == 1){
        		name = '工作';
        	}else if(day.type == 2){
        		name = '休息';
        	}else if(day.type == 3){
        		name = '年假';
        	}else if(day.type == 4){
        		name = '国假';
        	}
        	return name;
        }
        //点击日期动作，显示下拉框
        vm.clickDay = clickDay;
        function clickDay($event,day){
        	if(day.selected){
        		day.selected = false;
        	}else{
        		day.selected = true;
        		day.selectType = {key:day.type};
        	}
        }
        //显示日期
        vm.showDay = showDay;
        function showDay(day){
        	if(day.type == null || day.type == undefined){
        		return false;
        	}
        	if(day.selected){
        		return false;
        	}
        	return true;
        }
        //显示下拉框
        vm.showSelect = showSelect;
        function showSelect(day){
        	if(day.type == null || day.type == undefined){
        		return false;
        	}
        	if(day.selected){
        		return true;
        	}
        	return false;
        }
        //选择类型
        vm.selectType = selectType;
        vm.isSaving = false;
        function selectType(day){
        	if(day.selectType){
        		vm.isSaving = true;
        		var saveType = day.selectType.key;
        		var holidayInfo = {};
        		holidayInfo.type = day.selectType.key;
        		holidayInfo.currDay = DateUtils.convertLocalDateToFormat(new Date(vm.year,vm.month -  1,day.day),"yyyyMMdd");
        		HolidayInfo.updateByOne(holidayInfo, 
	        		function (result) {
        				vm.isSaving = false;
        				console.log(result);
	        			day.type = saveType;
	        			day.selected = undefined;
	            		day.selectType = undefined;
	                }, function (result) {
	                	vm.isSaving = false;
	                	console.log(result);
	                });
        	}else{
        		day.selected = undefined;
        		day.selectType = undefined;
        	}
        }
        //失去焦点
        vm.blurSelect = blurSelect;
        function blurSelect(day){
        	if(!vm.isSaving){
        		day.selected = undefined;
        		day.selectType = undefined;
        	}
        }
    }
})();
