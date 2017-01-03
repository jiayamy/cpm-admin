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
        var workDay = pagingParams.workDay;
        if(workDay && workDay.length == 8){
        	workDay = new Date(workDay.substring(0,4),parseInt(workDay.substring(4,6))-1,workDay.substring(6,8));
        }
        var type = pagingParams.type;
        if(type){
        	if(type == 1){
        		type = { id: 1, name: '公共成本' };
        	}else if(type == 2){
        		type = { id: 2, name: '合同' };
        	}else if(type == 3){
        		type = { id: 3, name: '项目' };
        	}
        }
        vm.searchQuery.workDay= workDay;
        vm.searchQuery.type = type;
        vm.searchQuery.objName = pagingParams.objName;
        vm.currentSearch = {};
        vm.currentSearch.workDay = workDay;
        vm.currentSearch.type = type;
        vm.currentSearch.objName = pagingParams.objName;
        if (!vm.currentSearch.workDay && !vm.currentSearch.type && !vm.currentSearch.objName){
        	vm.currentSearch.haveSearch = null;
        }else{
        	vm.currentSearch.haveSearch = true;
        }
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        
        vm.types = [{ id: 1, name: '公共成本' }, { id: 2, name: '合同' }, { id: 3, name: '项目' }];
        
        loadAll();

        function loadAll () {
        	if(!pagingParams.workDay){
        		pagingParams.workDay = "";
        	}
        	if(!pagingParams.type){
        		pagingParams.type = "";
        	}
        	if(!pagingParams.objName){
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
        				data[i].type = "公共成本";
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
                workDay: DateUtils.convertLocalDateToFormat(vm.currentSearch.workDay,"yyyyMMdd"),
                type:vm.currentSearch.type != null ? vm.currentSearch.type.id : "",
                objName:vm.currentSearch.objName
            });
        }

        function search(searchQuery) {
            if (!searchQuery.workDay && !searchQuery.type && !searchQuery.objName){
                return vm.clear();
            }
            vm.links = null;
            vm.page = 1;
            vm.predicate = '_score';
            vm.reverse = false;
            vm.currentSearch = searchQuery;
            vm.currentSearch.haveSearch = true;
            vm.transition();
        }

        function clear() {
            vm.links = null;
            vm.page = 1;
            vm.predicate = 'id';
            vm.reverse = true;
            vm.currentSearch = {};
            vm.currentSearch.haveSearch = null;
            vm.transition();
        }
        vm.datePickerOpenStatus.workDay = false;
        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
