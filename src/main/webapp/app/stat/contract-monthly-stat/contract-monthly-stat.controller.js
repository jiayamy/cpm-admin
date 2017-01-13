(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ContractMonthlyStatController', ContractMonthlyStatController);

    ContractMonthlyStatController.$inject = ['$scope', '$state', 'DateUtils','ContractMonthlyStat', 'ContractMonthlyStatSearch', 'ParseLinks', 'AlertService', 'paginationConstants', 'pagingParams'];

    function ContractMonthlyStatController ($scope, $state,DateUtils, ContractMonthlyStat, ContractMonthlyStatSearch, ParseLinks, AlertService, paginationConstants, pagingParams) {
        var vm = this;

        vm.loadPage = loadPage;
        vm.predicate = pagingParams.predicate;
        vm.reverse = pagingParams.ascending;
        vm.transition = transition;
        vm.itemsPerPage = 10;
        vm.clear = clear;
        vm.search = search;
        vm.loadAll = loadAll;
        vm.searchQuery = {};
        vm.currentSearch = pagingParams.search;
        var fromDate = pagingParams.fromDate;
        var toDate = pagingParams.toDate;
        var statDate = pagingParams.statDate;
        if(fromDate && fromDate.length == 8){
        	fromDate = new Date(fromDate.substring(0,4),parseInt(fromDate.substring(4,6))-1,fromDate.substring(6,8));
        }
        if(toDate && toDate.length == 8){
        	toDate = new Date(toDate.substring(0,4),parseInt(toDate.substring(4,6))-1,toDate.substring(6,8));
        }
        if(statDate && statDate.length == 8){
        	statDate = new Date(statDate.substring(0,4),parseInt(statDate.substring(4,6))-1,statDate.substring(6,8));
        }
        vm.searchQuery.fromDate= fromDate;
        vm.searchQuery.toDate = toDate;
        vm.searchQuery.statDate = statDate;
        if (!vm.searchQuery.fromDate && !vm.searchQuery.toDate && !vm.searchQuery.statDate){
        	vm.haveSearch = null;
        }else{
        	vm.haveSearch = true;
        }
        loadAll();

        function loadAll () {
        	if(pagingParams.fromDate == undefined){
        		pagingParams.fromDate = "";
        	}
        	if(pagingParams.toDate == undefined){
        		pagingParams.toDate = "";
        	}
        	if(pagingParams.statDate == undefined){
        		pagingParams.statDate = "";
        	}
        	ContractMonthlyStat.query({
                page: pagingParams.page - 1,
                size: vm.itemsPerPage,
                sort: sort(),
                fromDate : pagingParams.fromDate,
                toDate : pagingParams.toDate,
                statDate : pagingParams.statDate
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
                vm.contractMonthlyStats = data;
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
                fromDate: DateUtils.convertLocalDateToFormat(vm.searchQuery.fromDate,"yyyyMMdd"),
                toDate: DateUtils.convertLocalDateToFormat(vm.searchQuery.toDate,"yyyyMMdd"),
                statDate: DateUtils.convertLocalDateToFormat(vm.searchQuery.statDate,"yyyyMMdd"),
            });
        }

        function search(searchQuery) {
        	if (!vm.searchQuery.workDay && !vm.searchQuery.toDate && !vm.searchQuery.statDate){
                return vm.clear();
            }
            vm.links = null;
            vm.page = 1;
            vm.predicate = 'id';
            vm.reverse = false;
            vm.haveSearch = true;
            vm.transition();
        }

        function clear() {
            vm.links = null;
            vm.page = 1;
            vm.predicate = 'id';
            vm.reverse = true;
            vm.searchQuery = {};
            vm.haveSearch = null;
            vm.transition();
        }
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.datePickerOpenStatus.fromDate = false;
        vm.datePickerOpenStatus.toDate = false;
        vm.datePickerOpenStatus.statDate = false;
        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
