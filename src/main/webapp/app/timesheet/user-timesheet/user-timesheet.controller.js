(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('UserTimesheetController', UserTimesheetController);

    UserTimesheetController.$inject = ['$scope', '$state','DateUtils', 'UserTimesheet', 'ParseLinks', 'AlertService', 'paginationConstants', 'pagingParams'];

    function UserTimesheetController ($scope, $state, DateUtils, UserTimesheet, ParseLinks, AlertService, paginationConstants, pagingParams) {
        var vm = this;
        vm.loadPage = loadPage;
        vm.predicate = pagingParams.predicate;
        vm.reverse = pagingParams.ascending;
        vm.transition = transition;
        vm.itemsPerPage = paginationConstants.itemsPerPage;
        vm.clear = clear;
        vm.search = search;
        vm.loadAll = loadAll;
        vm.searchQuery = {}
        var type = pagingParams.type;
        if(type){
        	if(type == 1){
        		type = { id: 1, name: '无具体项目' };
        	}else if(type == 2){
        		type = { id: 2, name: '合同' };
        	}else if(type == 3){
        		type = { id: 3, name: '项目' };
        	}
        }
        vm.searchQuery.workDay= DateUtils.convertDayToDate(pagingParams.workDay);
        vm.searchQuery.type = type;
        vm.searchQuery.objName = pagingParams.objName;
        if (!vm.searchQuery.workDay && !vm.searchQuery.type && !vm.searchQuery.objName){
        	vm.haveSearch = null;
        }else{
        	vm.haveSearch = true;
        }
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        
        vm.types = [{ id: 1, name: '无具体项目' }, { id: 2, name: '合同' }, { id: 3, name: '项目' }];
        
        loadAll();

        function loadAll () {
        	if(pagingParams.workDay == undefined){
        		pagingParams.workDay = "";
        	}
        	if(pagingParams.type == undefined){
        		pagingParams.type = "";
        	}
        	if(pagingParams.objName == undefined){
        		pagingParams.objName = "";
        	}
            UserTimesheet.query({
            	workDay: pagingParams.workDay,
            	type: pagingParams.type,
            	objName: pagingParams.objName,
                page: pagingParams.page - 1,
                size: vm.itemsPerPage,
                sort: sort()
            }, onSuccess, onError);
            function sort() {
                var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
                if (vm.predicate !== 'id') {
                    result.push('id');
                }
                return result;
            }
            function onSuccess(data, headers) {
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
                vm.queryCount = vm.totalItems;
                vm.userTimesheets = handleData(data);
                vm.page = pagingParams.page;
            }
            function onError(error) {
                AlertService.error(error.data.message);
            }
        }
        function handleData(data){
        	if(data.length > 0){
        		for(var i = 0; i< data.length ; i++){
        			if(data[i].type == 1){
        				data[i].type = "无具体项目";
        			}else if(data[i].type == 2){
        				data[i].type = "合同";
        			}else if(data[i].type == 3){
        				data[i].type = "项目";
        			}
        			if(data[i].status == 1){
        				data[i].status = "正常";
        			}else if(data[i].status == 2){
        				data[i].status = "删除";
        			}
        		}
        	}
        	return data;
        }
        function loadPage(page) {
            vm.page = page;
            vm.transition();
        }

        function transition() {
            $state.transitionTo($state.$current, {
                page: vm.page,
                sort: vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc'),
                workDay: DateUtils.convertLocalDateToFormat(vm.searchQuery.workDay,"yyyyMMdd"),
                type:vm.searchQuery.type != null ? vm.searchQuery.type.id : "",
                objName:vm.searchQuery.objName
            });
        }

        function search() {
            if (!vm.searchQuery.workDay && !vm.searchQuery.type && !vm.searchQuery.objName){
                return vm.clear();
            }
            vm.links = null;
            vm.page = 1;
            vm.predicate = 'workDay';
            vm.reverse = false;
            vm.haveSearch = true;
            vm.transition();
        }

        function clear() {
            vm.links = null;
            vm.page = 1;
            vm.predicate = 'workDay';
            vm.reverse = false;
            vm.searchQuery = {};
            vm.haveSearch = null;
            vm.transition();
        }
        vm.datePickerOpenStatus.workDay = false;
        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
