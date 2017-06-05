(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ProjectTimesheetDialogController', ProjectTimesheetDialogController);

    ProjectTimesheetDialogController.$inject = ['$timeout','$state','AlertService','DateUtils', '$scope', '$stateParams','previousState', 'entity', 'ProjectTimesheet'];

    function ProjectTimesheetDialogController ($timeout,$state,AlertService,DateUtils, $scope, $stateParams,previousState, entity, ProjectTimesheet) {
    	var vm = this;
        vm.previousState = previousState.name;
        vm.userTimesheet = entity;
        vm.save = save;
        
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.searchQuery = {};
        vm.searchQuery.workDay = DateUtils.convertDayToDate(entity.workDay);
        vm.search = search;
        vm.noData = true;
        vm.isNotHoliday = isNotHoliday;
        function isNotHoliday(data){
        	if(data.length > 8){
        		return true;
        	}
        	return false;
        }
        vm.isNotSame = isNotSame;
        function isNotSame(data,check){
        	data = parseFloat(data);
        	check = parseFloat(check);
        	if(check != data){
        		return true;
        	}
        	return false;
        }
        loadAll();
        function search(){
        	loadAll();
        }
        function loadAll () {
        	var workDay = "";
        	if(vm.searchQuery.workDay){
        		workDay = DateUtils.convertLocalDateToFormat(vm.searchQuery.workDay,"yyyyMMdd");
        	}
        	ProjectTimesheet.getEditUserTimesheets({
            	workDay: workDay,
            	id:entity.id,
                page: 1,
                size: 0,
                sort: sort()
            }, onSuccess, onError);
            
            function sort() {
                var result = [];
                result.push('id');
                return result;
            }
            function onSuccess(data, headers) {
                handleData(data);
            }
            function onError(error) {
                AlertService.error(error.data.message);
            }
        }
        function handleData(data){
        	var days = [];
        	var userTimesheets = [];
        	if(data.length > 1){
        		vm.noData = false;
        	}else{
        		vm.noData = true;
        	}
        	if(data.length > 0){
        		for(var i = 0; i< data.length ; i++){
        			if(data[i].type == -1){
        				data[i].userName = "日期";
        			}
        			if(data[i].inputType == "加班工时"){
        				data[i].userName = "";
        			}
        			if(i == 0){
        				days.push(data[i]);
        			}else{
        				userTimesheets.push(data[i]);
        			}
        		}
        	}
        	vm.days = days;
        	vm.userTimesheets = userTimesheets;
        }
        function save () {
            vm.isSaving = true;
            var updateDatas = [];
            //校验
            if(vm.days && vm.days.length > 0){
            	for(var i = 0; i< vm.days.length ; i++){
            		updateDatas.push(vm.days[i]);
            	}
            }
            //校验工时是否为数字或小数点
            var cd1 = 0;
            var cd2 = 0;
            var cd3 = 0;
            var cd4 = 0;
            var cd5 = 0;
            var cd6 = 0;
            var cd7 = 0;
            var tmp = null;
            var reg = /^(\d{1}(\.\d{1})?)$/;
            
            //加班工时
            var cds1 = 0;
            var cds2 = 0;
            var cds3 = 0;
            var cds4 = 0;
            var cds5 = 0;
            var cds6 = 0;
            var cds7 = 0;
            var tmps = null;
            
            var d1 = 0;
            var d2 = 0;
            var d3 = 0;
            var d4 = 0;
            var d5 = 0;
            var d6 = 0;
            var d7 = 0;
            
            var ds1 = 0;
            var ds2 = 0;
            var ds3 = 0;
            var ds4 = 0;
            var ds5 = 0;
            var ds6 = 0;
            var ds7 = 0;
            if(vm.userTimesheets && vm.userTimesheets.length > 0){
            	for(var i = 0; i< vm.userTimesheets.length ; i++){
            		tmp = vm.userTimesheets[i];
            		//校验是否为空
            		if (tmp.inputType == "正常工时") {
						if (tmp.check1 == undefined) {
							tmp.check1 = "0";
						}
						if (tmp.check2 == undefined) {
							tmp.check2 = "0";
						}
						if (tmp.check3 == undefined) {
							tmp.check3 = "0";
						}
						if (tmp.check4 == undefined) {
							tmp.check4 = "0";
						}
						if (tmp.check5 == undefined) {
							tmp.check5 = "0";
						}
						if (tmp.check6 == undefined) {
							tmp.check6 = "0";
						}
						if (tmp.check7 == undefined) {
							tmp.check7 = "0";
						}
						if (!tmp.check1 || !tmp.check2 || !tmp.check3
								|| !tmp.check4 || !tmp.check5 || !tmp.check6
								|| !tmp.check7) {
							AlertService
									.error("cpmApp.projectTimesheet.save.dataNull");
							vm.isSaving = false;
							return false;
						}
						//校验是否为小数后1位
						if (!reg.test(tmp.check1) || !reg.test(tmp.check2)
								|| !reg.test(tmp.check3)
								|| !reg.test(tmp.check4)
								|| !reg.test(tmp.check5)
								|| !reg.test(tmp.check6)
								|| !reg.test(tmp.check7)) {
							AlertService
									.error("cpmApp.projectTimesheet.save.dataError");
							vm.isSaving = false;
							return false;
						}
						//校验是否大于0或者小于8
						cd1 = parseFloat(tmp.check1);
						cd2 = parseFloat(tmp.check2);
						cd3 = parseFloat(tmp.check3);
						cd4 = parseFloat(tmp.check4);
						cd5 = parseFloat(tmp.check5);
						cd6 = parseFloat(tmp.check6);
						cd7 = parseFloat(tmp.check7);
						if (cd1 < 0 || cd1 > 8 || cd2 < 0 || cd2 > 8 || cd3 < 0
								|| cd3 > 8 || cd4 < 0 || cd4 > 8 || cd5 < 0
								|| cd5 > 8 || cd6 < 0 || cd6 > 8 || cd7 < 0
								|| cd7 > 8) {
							AlertService
									.error("cpmApp.projectTimesheet.save.dataError");
							vm.isSaving = false;
							return false;
						}
						//校验原始数据
						if (!reg.test(tmp.data1) || !reg.test(tmp.data2)
								|| !reg.test(tmp.data3) || !reg.test(tmp.data4)
								|| !reg.test(tmp.data5) || !reg.test(tmp.data6)
								|| !reg.test(tmp.data7)) {
							AlertService
									.error("cpmApp.projectTimesheet.save.dataError");
							vm.isSaving = false;
							return false;
						}
						d1 = parseFloat(tmp.data1);
						d2 = parseFloat(tmp.data2);
						d3 = parseFloat(tmp.data3);
						d4 = parseFloat(tmp.data4);
						d5 = parseFloat(tmp.data5);
						d6 = parseFloat(tmp.data6);
						d7 = parseFloat(tmp.data7);
						if ((d1 == 0 && cd1 > 0) || (d2 == 0 && cd2 > 0)
								|| (d3 == 0 && cd3 > 0) || (d4 == 0 && cd4 > 0)
								|| (d5 == 0 && cd5 > 0) || (d6 == 0 && cd6 > 0)
								|| (d7 == 0 && cd7 > 0)) {
							AlertService
									.error("cpmApp.projectTimesheet.save.inputZeroCheck");
							vm.isSaving = false;
							return false;
						}
						if (cd1 > d1 || cd2 > d2 || cd3 > d3 || cd4 > d4
								|| cd5 > d5 || cd6 > d6 || cd7 > d7) {
							AlertService
									.error("cpmApp.projectTimesheet.save.overInput");
							vm.isSaving = false;
							return false;
						}
						updateDatas.push(vm.userTimesheets[i]);
					}
            		//加班工时
            		if (tmp.inputType == "加班工时") {
						if (tmp.check1 == undefined) {
							tmp.check1 = "0";
						}
						if (tmp.check2 == undefined) {
							tmp.check2 = "0";
						}
						if (tmp.check3 == undefined) {
							tmp.check3 = "0";
						}
						if (tmp.check4 == undefined) {
							tmp.check4 = "0";
						}
						if (tmp.check5 == undefined) {
							tmp.check5 = "0";
						}
						if (tmp.check6 == undefined) {
							tmp.check6 = "0";
						}
						if (tmp.check7 == undefined) {
							tmp.check7 = "0";
						}
						if (!tmp.check1 || !tmp.check2 || !tmp.check3
								|| !tmp.check4 || !tmp.check5 || !tmp.check6
								|| !tmp.check7) {
							AlertService
									.error("cpmApp.projectTimesheet.save.dataNull");
							vm.isSaving = false;
							return false;
						}
						//校验是否为小数后1位
						if (!reg.test(tmp.check1) || !reg.test(tmp.check2)
								|| !reg.test(tmp.check3)
								|| !reg.test(tmp.check4)
								|| !reg.test(tmp.check5)
								|| !reg.test(tmp.check6)
								|| !reg.test(tmp.check7)) {
							AlertService
									.error("cpmApp.projectTimesheet.save.dataExtraError");
							vm.isSaving = false;
							return false;
						}
						//校验是否大于2或者小于8
						cds1 = parseFloat(tmp.check1);
						cds2 = parseFloat(tmp.check2);
						cds3 = parseFloat(tmp.check3);
						cds4 = parseFloat(tmp.check4);
						cds5 = parseFloat(tmp.check5);
						cds6 = parseFloat(tmp.check6);
						cds7 = parseFloat(tmp.check7);
						if ((cds1 < 2 && cds1 != 0) || cds1 > 8 || (cds2 < 2 && cds2 != 0) || cds2 > 8 || (cds3 < 2 && cds3 != 0)
								|| cds3 > 8 || (cds4 < 2 && cds4 != 0) || cds4 > 8 || (cds5 < 2 && cds5 != 0)
								|| cds5 > 8 || (cds6 < 2 && cds6 != 0) || cds6 > 8 || (cds7 < 2 && cds7 != 0)
								|| cds7 > 8) {
							AlertService
									.error("cpmApp.projectTimesheet.save.dataExtraError");
							vm.isSaving = false;
							return false;
						}
						//校验原始数据
						if (!reg.test(tmp.data1) || !reg.test(tmp.data2)
								|| !reg.test(tmp.data3) || !reg.test(tmp.data4)
								|| !reg.test(tmp.data5) || !reg.test(tmp.data6)
								|| !reg.test(tmp.data7)) {
							AlertService
									.error("cpmApp.projectTimesheet.save.dataExtraError");
							vm.isSaving = false;
							return false;
						}
						ds1 = parseFloat(tmp.data1);
						ds2 = parseFloat(tmp.data2);
						ds3 = parseFloat(tmp.data3);
						ds4 = parseFloat(tmp.data4);
						ds5 = parseFloat(tmp.data5);
						ds6 = parseFloat(tmp.data6);
						ds7 = parseFloat(tmp.data7);
						if ((ds1 == 0 && cds1 > 0) || (ds2 == 0 && cds2 > 0)
								|| (ds3 == 0 && cds3 > 0) || (ds4 == 0 && cds4 > 0)
								|| (ds5 == 0 && cds5 > 0) || (ds6 == 0 && cds6 > 0)
								|| (ds7 == 0 && cds7 > 0)) {
							AlertService
									.error("cpmApp.projectTimesheet.save.inputZeroCheck");
							vm.isSaving = false;
							return false;
						}
						if (cds1 > ds1 || cds2 > ds2 || cds3 > ds3 || cds4 > ds4
								|| cds5 > ds5 || cds6 > ds6 || cds7 > ds7) {
							AlertService
									.error("cpmApp.projectTimesheet.save.overExtraInput");
							vm.isSaving = false;
							return false;
						}
						updateDatas.push(vm.userTimesheets[i]);
					}
            	}
            }
            ProjectTimesheet.update(updateDatas, onSaveSuccess, onSaveError);
            return false;
        }
        function onSaveSuccess (result) {
        	vm.isSaving = false;
        	if(result.data){
        		AlertService.error(result.data.message);
        	}else if(result.message){
        		if(result.message == "cpmApp.contractTimesheet.save.success"){
        			AlertService.success(result.message);
        			$state.go("project-timesheet", null, { reload: true });
        		}else if(result.message && result.param){
        			var param = {};
        			param.param = result.param;
        			AlertService.error(result.message,param);
        		}else{
        			AlertService.error(result.message);
        		}
        	}
        }

        function onSaveError (result) {
            vm.isSaving = false;
            if(result.data){
        		AlertService.error(result.data.message);
        	}else if(result.message){
        		AlertService.error(result.message);
        	}
        }
        
        vm.datePickerOpenStatus.workDay = false;
        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
