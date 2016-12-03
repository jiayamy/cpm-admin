(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('UserTimesheetDialogController', UserTimesheetDialogController);

    UserTimesheetDialogController.$inject = ['$timeout', '$scope', '$stateParams','previousState', 'entity', 'UserTimesheetSearch'];

    function UserTimesheetDialogController ($timeout, $scope, $stateParams,previousState, entity, UserTimesheetSearch) {
        var vm = this;

        vm.previousState = previousState.name;
        vm.userTimesheet = entity;
        vm.save = save;
        loadAll();
        function loadAll () {
        	var workDay = "";
        	if(entity && entity.workDay){
        		workDay = entity.workDay;
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
                vm.userTimesheets = handleData(data);
            }
            function onError(error) {
                AlertService.error(error.data.message);
            }
        }
        function handleData(data){
        	if(data.length > 0){
        		for(var i = 0; i< data.length ; i++){
        			if(data[i].type == 1){
        				data[i].type = "公共成本";
        			}else if(data[i].type == 2){
        				data[i].type = "合同";
        			}else if(data[i].type == 3){
        				data[i].type = "项目";
        			}
        		}
        	}
        	return data;
        }
        function save () {
            vm.isSaving = true;
            if (vm.userTimesheet.id !== null) {
            	UserTimesheetSearch.update(vm.userTimesheets, onSaveSuccess, onSaveError);
            } else {
            	UserTimesheetSearch.update(vm.userTimesheets, onSaveSuccess, onSaveError);
            }
            return false;
        }
        function onSaveSuccess (result) {
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
            AlertService.error(error.data.message);
        }
    }
})();
