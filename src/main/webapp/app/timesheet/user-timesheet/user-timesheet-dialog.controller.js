(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('UserTimesheetDialogController', UserTimesheetDialogController);

    UserTimesheetDialogController.$inject = ['$timeout','$state','AlertService','DateUtils', '$scope', '$stateParams','previousState', 'entity', 'UserTimesheetSearch','WorkArea'];

    function UserTimesheetDialogController ($timeout,$state,AlertService,DateUtils, $scope, $stateParams,previousState, entity, UserTimesheetSearch, WorkArea) {
        var vm = this;

        vm.previousState = previousState.name;
        vm.userTimesheet = entity;
        vm.save = save;
        vm.arrowLeft = arrowLeft;
        vm.arrowRight = arrowRight;
        vm.allAreas = [];
        
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.searchQuery = {};
        vm.searchQuery.workDay = DateUtils.convertDayToDate(entity.workDay);
        if(vm.searchQuery.workDay == null){
        	vm.searchQuery.workDay = new Date();
        }
        vm.search = search;
        vm.isNotHoliday = isNotHoliday;
        /**
         * 往前加一周时间
         */
        function arrowLeft(){
        	var date = DateUtils.convertLocalDateToFormat(vm.searchQuery.workDay,"yyyyMMdd");
        	vm.searchQuery.workDay = new Date(date.substring(0,4),parseInt(date.substring(4,6))-1,parseInt(date.substring(6,8))-7);
        	vm.search();
        }
        /**
         * 往后加一周时间
         */
        function arrowRight(){
        	var date = DateUtils.convertLocalDateToFormat(vm.searchQuery.workDay,"yyyyMMdd");
        	vm.searchQuery.workDay = new Date(date.substring(0,4),parseInt(date.substring(4,6))-1,parseInt(date.substring(6,8))+7);
        	vm.search();
        }
        function isNotHoliday(data){
        	if(data.length > 8){
        		return true;
        	}
        	return false;
        }
        
        loadWorkArea();
        loadAll();
        
        function search(){
        	loadAll();
        }
        function loadWorkArea(){
        	WorkArea.queryAll({},function onSuccess(data, headers) {
        		vm.allAreas = data;
        	})
        }
        function loadAll () {
        	var workDay = "";
        	if(vm.searchQuery.workDay){
        		workDay = DateUtils.convertLocalDateToFormat(vm.searchQuery.workDay,"yyyyMMdd");
        	}
        	UserTimesheetSearch.query({
            	workDay: workDay,
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
        	var areas = [];
        	var userTimesheets = [];
        	if(data.length > 0){
        		for(var i = 0; i< data.length ; i++){
        			if(data[i].type == -1){
        				data[i].typeName = "日期";
        			}else if(data[i].type == -2){
        				data[i].typeName = "工作地点";
        			}else if(data[i].type == 1){
        				data[i].typeName = "无具体项目";
        				if(data[i].inputType == "加班工时"){
        					data[i].typeName = "";
        					data[i].objName1 = "";
        				}
        			}else if(data[i].type == 2){
        				data[i].typeName = "合同";
        				data[i].objName1 = data[i].objName;
        				if(data[i].inputType == "加班工时"){
        					data[i].typeName = "";
        					data[i].objName1 = "";
        				}
        			}else if(data[i].type == 3){
        				data[i].typeName = "项目";
        				data[i].objName1 = data[i].objName;
        				if(data[i].inputType == "加班工时"){
        					data[i].typeName = "";
        					data[i].objName1 = "";
        				}
        			}
        			if(i == 0){
        				days.push(data[i]);
        			}else if(i == 1){
        				areas.push(data[i]);
        			}else{
        				userTimesheets.push(data[i]);
        			}
        		}
        	}
        	vm.days = days;
        	vm.areas = areas;
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
            if(vm.areas && vm.areas.length > 0){
            	for(var i = 0; i< vm.areas.length ; i++){
            		updateDatas.push(vm.areas[i]);
            	}
            }
            //校验 工时、加班工时 是否为数字或小数点
            //工时
            var td1 = 0;
            var td2 = 0;
            var td3 = 0;
            var td4 = 0;
            var td5 = 0;
            var td6 = 0;
            var td7 = 0;
            var tmp = null;
            //加班工时
            var tds1 = 0;
            var tds2 = 0;
            var tds3 = 0;
            var tds4 = 0;
            var tds5 = 0;
            var tds6 = 0;
            var tds7 = 0;
            var tmps = null;
            
            var reg = /^(\d{1}(\.\d{1})?)$/;
            
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
            		if (tmp.inputType == "正常工时") {//正常工时
						if (tmp.data1 == undefined) {
							tmp.data1 = "0";
						}
						if (tmp.data2 == undefined) {
							tmp.data2 = "0";
						}
						if (tmp.data3 == undefined) {
							tmp.data3 = "0";
						}
						if (tmp.data4 == undefined) {
							tmp.data4 = "0";
						}
						if (tmp.data5 == undefined) {
							tmp.data5 = "0";
						}
						if (tmp.data6 == undefined) {
							tmp.data6 = "0";
						}
						if (tmp.data7 == undefined) {
							tmp.data7 = "0";
						}
						if(!tmp.data1 || !tmp.data2 || !tmp.data3 || !tmp.data4 
	            				|| !tmp.data5|| !tmp.data6|| !tmp.data7){
	            			vm.isSaving = false;
	            			AlertService.error("cpmApp.userTimesheet.save.dataNull");
	            			return false;
	            		}
	            		//校验是否为小数后1位
	            		if(!reg.test(tmp.data1) || !reg.test(tmp.data2) || !reg.test(tmp.data3) || !reg.test(tmp.data4) 
	            				|| !reg.test(tmp.data5)||!reg.test(tmp.data6)||!reg.test(tmp.data7)){
	            			vm.isSaving = false;
	            			AlertService.error("cpmApp.userTimesheet.save.dataError");
	            			return false;
	            		}
	            		//校验是否大于0或者小于8
	            		d1 = parseFloat(tmp.data1);
	            		d2 = parseFloat(tmp.data2);
	            		d3 = parseFloat(tmp.data3);
	            		d4 = parseFloat(tmp.data4);
	            		d5 = parseFloat(tmp.data5);
	            		d6 = parseFloat(tmp.data6);
	            		d7 = parseFloat(tmp.data7);
	            		if(d1 < 0 || d1 > 8 || d2 < 0 || d2 > 8 || d3 < 0 || d3 > 8 || d4 < 0 || d4 > 8
	            				 || d5 < 0 || d5 > 8 || d6 < 0 || d6 > 8 || d7 < 0 || d7 > 8){
	            			vm.isSaving = false;
	            			AlertService.error("cpmApp.userTimesheet.save.dataError");
	            			return false;
	            		}
	            		//校验一天的日报工时是否大于8
	            		td1 += d1;
	            		td2 += d2;
	            		td3 += d3;
	            		td4 += d4;
	            		td5 += d5;
	            		td6 += d6;
	            		td7 += d7;
	            		if(td1 > 8 || td2 > 8 || td3 > 8 || td4 > 8
	            				 || td5 > 8 || td6 > 8 || td7 > 8){
	            			vm.isSaving = false;
	            			AlertService.error("cpmApp.userTimesheet.save.dayDataMax");
	            			return false;
	            		}
	            		updateDatas.push(vm.userTimesheets[i]);
					}
					if (tmp.inputType == "加班工时") {//加班工时
						tmps = tmp;
						if (tmps.data1 == undefined) {
							tmps.data1 = "0";
						}
						if (tmps.data2 == undefined) {
							tmps.data2 = "0";
						}
						if (tmps.data3 == undefined) {
							tmps.data3 = "0";
						}
						if (tmps.data4 == undefined) {
							tmps.data4 = "0";
						}
						if (tmps.data5 == undefined) {
							tmps.data5 = "0";
						}
						if (tmps.data6 == undefined) {
							tmps.data6 = "0";
						}
						if (tmps.data7 == undefined) {
							tmps.data7 = "0";
						}
						if(!tmps.data1 || !tmps.data2 || !tmps.data3 || !tmps.data4 
	            				|| !tmps.data5|| !tmps.data6|| !tmps.data7){
	            			vm.isSaving = false;
	            			AlertService.error("cpmApp.userTimesheet.save.dataNull");
	            			return false;
	            		}
	            		//校验是否为小数后1位
	            		if(!reg.test(tmps.data1) || !reg.test(tmps.data2) || !reg.test(tmps.data3) || !reg.test(tmps.data4) 
	            				|| !reg.test(tmps.data5) || !reg.test(tmps.data6) || !reg.test(tmps.data7)){
	            			vm.isSaving = false;
	            			AlertService.error("cpmApp.userTimesheet.save.dataError");
	            			return false;
	            		}
	            		//校验是否大于2或者小于8
	            		ds1 = parseFloat(tmps.data1);
	            		ds2 = parseFloat(tmps.data2);
	            		ds3 = parseFloat(tmps.data3);
	            		ds4 = parseFloat(tmps.data4);
	            		ds5 = parseFloat(tmps.data5);
	            		ds6 = parseFloat(tmps.data6);
	            		ds7 = parseFloat(tmps.data7);
	            		if((ds1 < 2 && ds1 != 0) || ds1 > 8 || (ds2 < 2 && ds2 != 0) || ds2 > 8 || (ds3 < 2 && ds3 != 0) || ds3 > 8 || (ds4 < 2 && ds4 != 0) || ds4 > 8
	            				 || (ds5 < 2 && ds5 != 0) || ds5 > 8 || (ds6 < 2 && ds6 != 0) || ds6 > 8 || (ds7 < 2 && ds7 != 0) || ds7 > 8){
	            			vm.isSaving = false;
	            			AlertService.error("cpmApp.userTimesheet.save.extraDataError");
	            			return false;
	            		}
	            		//校验一天的日报加班工时是否大于8
	            		tds1 += ds1;
	            		tds2 += ds2;
	            		tds3 += ds3;
	            		tds4 += ds4;
	            		tds5 += ds5;
	            		tds6 += ds6;
	            		tds7 += ds7;
	            		if(tds1 > 8 || tds2 > 8 || tds3 > 8 || tds4 > 8
	            				 || tds5 > 8 || tds6 > 8 || tds7 > 8){
	            			vm.isSaving = false;
	            			AlertService.error("cpmApp.userTimesheet.save.dayExtraDataMax");
	            			return false;
	            		}
	            		updateDatas.push(vm.userTimesheets[i]);
					}
            	}
            }
            UserTimesheetSearch.update(updateDatas, onSaveSuccess, onSaveError);
            return false;
        }
        function onSaveSuccess (result) {
        	vm.isSaving = false;
        	if(result.data){
        		AlertService.error(result.data.message);
        	}else if(result.message){
        		if(result.message == "cpmApp.userTimesheet.save.success"){
        			AlertService.success(result.message);
        			$state.go("user-timesheet", null, { reload: true });
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
