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
            		if(!tmp.check1 || !tmp.check2 || !tmp.check3 || !tmp.check4 
            				|| !tmp.check5|| !tmp.check6|| !tmp.check7){
            			AlertService.error("cpmApp.projectTimesheet.save.dataNull");
            			vm.isSaving = false;
            			return false;
            		}
            		//校验是否为小数后1位
            		if(!reg.test(tmp.check1) || !reg.test(tmp.check2) || !reg.test(tmp.check3) || !reg.test(tmp.check4) 
            				|| !reg.test(tmp.check5)||!reg.test(tmp.check6)||!reg.test(tmp.check7)){
            			AlertService.error("cpmApp.projectTimesheet.save.dataError");
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
            		if(cd1 < 0 || cd1 > 8 || cd2 < 0 || cd2 > 8 || cd3 < 0 || cd3 > 8 || cd4 < 0 || cd4 > 8
            				 || cd5 < 0 || cd5 > 8 || cd6 < 0 || cd6 > 8 || cd7 < 0 || cd7 > 8){
            			AlertService.error("cpmApp.projectTimesheet.save.dataError");
            			vm.isSaving = false;
            			return false;
            		}
            		//校验原始数据
            		if(!reg.test(tmp.data1) || !reg.test(tmp.data2) || !reg.test(tmp.data3) || !reg.test(tmp.data4) 
            				|| !reg.test(tmp.data5)||!reg.test(tmp.data6)||!reg.test(tmp.data7)){
            			AlertService.error("cpmApp.projectTimesheet.save.dataError");
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
            		if((d1 == 0 && cd1 > 0) || (d2 == 0 && cd2 > 0) || (d3 == 0 && cd3 > 0) || (d4 == 0 && cd4 > 0)
            				 || (d5 == 0 && cd5 > 0) || (d6 == 0 && cd6 > 0) || (d7 == 0 && cd7 > 0)){
            			AlertService.error("cpmApp.projectTimesheet.save.inputZeroCheck");
            			vm.isSaving = false;
            			return false;
            		}
            		if(cd1 > d1 || cd2 > d2 || cd3 > d3 || cd4 > d4 
            				|| cd5 > d5 || cd6 > d6 || cd7 > d7){
            			AlertService.error("cpmApp.projectTimesheet.save.overInput");
            			vm.isSaving = false;
            			return false;
            		}
            		
            		updateDatas.push(vm.userTimesheets[i]);
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
