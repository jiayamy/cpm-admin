(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('HolidayInfoController', HolidayInfoController);

    HolidayInfoController.$inject = ['$scope', '$state','DateUtils', 'HolidayInfo', 'ParseLinks', 'AlertService', 'paginationConstants', 'pagingParams'];

    function HolidayInfoController ($scope, $state,DateUtils, HolidayInfo, ParseLinks, AlertService, paginationConstants, pagingParams) {
        var vm = this;

        vm.loadPage = loadPage;
        vm.predicate = pagingParams.predicate;
        vm.reverse = pagingParams.ascending;
        vm.transition = transition;
        vm.itemsPerPage = paginationConstants.itemsPerPage;
        
        vm.clear = clear;
        vm.search = search;
        vm.loadAll = loadAll;
        
        vm.searchQuery = {};
        vm.searchQuery.fromCurrDay = DateUtils.convertDayToDate(pagingParams.fromCurrDay);
        vm.searchQuery.toCurrDay = DateUtils.convertDayToDate(pagingParams.toCurrDay);
        
        if (!vm.searchQuery.fromCurrDay && !vm.searchQuery.toCurrDay){
        	vm.haveSearch = null;
        }else{
        	vm.haveSearch = true;
        }

        loadAll();

        function loadAll () {
            	if(pagingParams.fromCurrDay == undefined){
            		pagingParams.fromCurrDay = "";
            	}
            	if(pagingParams.toCurrDay == undefined){
            		pagingParams.toCurrDay = "";
            	}
                HolidayInfo.query({
                	fromCurrDay:pagingParams.fromCurrDay,
                	toCurrDay:pagingParams.toCurrDay,
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
                vm.holidayInfos = handleData(data);
                vm.page = pagingParams.page;
            }
            function onError(error) {
            	AlertService.error(error.headers("X-cpmApp-error"));
            }
        }
        
        function handleData(data){
        	if (data.length>0) {
				for (var i = 0; i < data.length; i++) {
					if (data[i].type == 1) {
						data[i].type = "正常工作日";
					} else if (data[i].type == 2) {
						data[i].type = "正常休息日";
					} else if (data[i].type == 3) {
						data[i].type = "年假";
					} else if (data[i].type == 4) {
						data[i].type = "国家假日";
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
                fromCurrDay:DateUtils.convertLocalDateToFormat(vm.searchQuery.fromCurrDay,"yyyyMMdd"),
                toCurrDay:DateUtils.convertLocalDateToFormat(vm.searchQuery.toCurrDay,"yyyyMMdd")
            });
        }

        function search() {
        	if (!vm.searchQuery.fromCurrDay && !vm.searchQuery.toCurrDay){
                return vm.clear();
            }
            vm.links = null;
            vm.page = 1;
            vm.predicate = 'currDay';
            vm.reverse = false;
            vm.haveSearch = true;
            if(vm.searchQuery.fromCurrDay && vm.searchQuery.toCurrDay ){
            	var fromDay = DateUtils.convertLocalDateToFormat(vm.searchQuery.fromCurrDay,"yyyyMMdd");
            	var toDay = DateUtils.convertLocalDateToFormat(vm.searchQuery.toCurrDay,"yyyyMMdd");
            	if(toDay < fromDay){
            		AlertService.error("cpmApp.holidayInfo.search.deadLineError");
            		return ;
            	}
            }
            vm.transition();
        }

        function clear() {
            vm.links = null;
            vm.page = 1;
            vm.predicate = 'currDay';
            vm.reverse = false;
            vm.haveSearch = false;
            vm.searchQuery = {}
            vm.transition();
        }
        
        vm.datePickerOpenStatus={};
        vm.openCalendar = openCalendar;
        vm.datePickerOpenStatus.fromCurrDay = false;
        vm.datePickerOpenStatus.toCurrDay = false;
        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
