(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('HolidayInfoController', HolidayInfoController);

    HolidayInfoController.$inject = ['$scope', '$state','DateUtils', 'HolidayInfo', 'HolidayInfoSearch', 'ParseLinks', 'AlertService', 'paginationConstants', 'pagingParams'];

    function HolidayInfoController ($scope, $state,DateUtils, HolidayInfo, HolidayInfoSearch, ParseLinks, AlertService, paginationConstants, pagingParams) {
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
        vm.searchQuery.fromCurrDay = DateUtils.convertDayToDate(pagingParams.fromCurrDay);;
        vm.searchQuery.toCurrDay = DateUtils.convertDayToDate(pagingParams.toCurrDay);;
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
                vm.holidayInfos = data;
                vm.page = pagingParams.page;
            }
            function onError(error) {
                AlertService.error(error.data.message);
            }
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
            vm.transition();
        }

        function clear() {
            vm.links = null;
            vm.page = 1;
            vm.predicate = 'currDay';
            vm.reverse = false;
            vm.haveSearch = false;
            vm.searchQuery.fromCurrDay = null;
            vm.searchQuery.toCurrDay = null;
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
