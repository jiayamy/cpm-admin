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
        
        vm.allAreas = [];
        
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.searchQuery = {};
        if(entity && entity.workDay){
        	var y = parseInt(entity.workDay/10000);
        	var m = parseInt((entity.workDay - y * 10000)/100) - 1;
        	var d = entity.workDay - y * 10000 - (m + 1) * 100;
        	vm.searchQuery.workDay = new Date(""+y,""+m,""+d);
        }
        vm.search = search;
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
        			}else if(data[i].type == 2){
        				data[i].typeName = "合同";
        			}else if(data[i].type == 3){
        				data[i].typeName = "项目";
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
            //校验工时是否为数字或小数点
            var td1 = 0;
            var td2 = 0;
            var td3 = 0;
            var td4 = 0;
            var td5 = 0;
            var td6 = 0;
            var td7 = 0;
            var tmp = null;
            var reg = /^(\d{1}(\.\d{1})?)$/;
            
            var d1 = 0;
            var d2 = 0;
            var d3 = 0;
            var d4 = 0;
            var d5 = 0;
            var d6 = 0;
            var d7 = 0;
            if(vm.userTimesheets && vm.userTimesheets.length > 0){
            	for(var i = 0; i< vm.userTimesheets.length ; i++){
            		tmp = vm.userTimesheets[i];
            		//校验是否为空
            		if(!tmp.data1 || !tmp.data2 || !tmp.data3 || !tmp.data4 
            				|| !tmp.data5|| !tmp.data6|| !tmp.data7){
            			AlertService.error("cpmApp.userTimesheet.save.dataNull");
            			return false;
            		}
            		//校验是否为小数后1位
            		if(!reg.test(tmp.data1) || !reg.test(tmp.data2) || !reg.test(tmp.data3) || !reg.test(tmp.data4) 
            				|| !reg.test(tmp.data5)||!reg.test(tmp.data6)||!reg.test(tmp.data7)){
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
            			AlertService.error("cpmApp.userTimesheet.save.dayDataMax");
            			return false;
            		}
            		updateDatas.push(vm.userTimesheets[i]);
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
