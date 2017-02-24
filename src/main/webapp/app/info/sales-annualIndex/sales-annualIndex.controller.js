(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('SalesAnnualIndexController', SalesAnnualIndexController);

    SalesAnnualIndexController.$inject = ['$scope','$rootScope', '$state', 'SalesAnnualIndex',  'ParseLinks', 'AlertService', 'paginationConstants', 'pagingParams', 'DateUtils'];

    function SalesAnnualIndexController ($scope,$rootScope, $state, SalesAnnualIndex, ParseLinks, AlertService, paginationConstants, pagingParams, DateUtils) {
        var vm = this;
        console.log(vm);
        vm.loadPage = loadPage;
        vm.predicate = pagingParams.predicate;
        vm.reverse = pagingParams.ascending;
        vm.transition = transition;
        vm.itemsPerPage = paginationConstants.itemsPerPage;
        
        vm.clear = clear;
        vm.search = search;
        
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        
        vm.loadAll = loadAll;
        vm.searchQuery = {};
        vm.searchQuery.statYear= DateUtils.convertYYYYToDate(pagingParams.statYear);
        vm.searchQuery.userId = pagingParams.userId;
        vm.searchQuery.userName = pagingParams.userName;
        if (!vm.searchQuery.statYear && !vm.searchQuery.userId){
        	vm.haveSearch = null;
        }else{
        	vm.haveSearch = true;
        }
        //加载列表
        loadAll();
        function loadAll () {
            SalesAnnualIndex.query({
            	statYear: pagingParams.statYear,
            	userId: pagingParams.userId,
                page: pagingParams.page - 1,
                size: vm.itemsPerPage,
                sort: sort()
            }, onSuccess, onError);
            function sort() {
                var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
                if (vm.predicate !== 'wsai.id') {
                    result.push('wsai.id,desc');
                }
                return result;
            }
            function onSuccess(data, headers) {
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
                vm.queryCount = vm.totalItems;
                vm.salesAnnualIndexs = handleData(data);
                vm.page = pagingParams.page;
            }
            function onError(error) {
                AlertService.error(error.data.message);
            }
            function handleData(data){
            	return data;
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
                statYear: DateUtils.convertLocalDateToFormat(vm.searchQuery.statYear,"yyyy"),
                userId: vm.searchQuery.userId,
                userName: vm.searchQuery.userName
            });
        }

        function search() {
        	if (!vm.searchQuery.statYear && !vm.searchQuery.userId){
                return vm.clear();
            }
            vm.links = null;
            vm.page = 1;
            vm.predicate = 'wsai.statYear';
            vm.reverse = false;
            vm.haveSearch = true;
            vm.transition();
        }

        function clear() {
            vm.links = null;
            vm.page = 1;
            vm.predicate = 'wsai.statYear';
            vm.reverse = false;
            vm.searchQuery = {};
            vm.haveSearch = null;
            vm.transition();
        }
        vm.datePickerOpenStatus.originYear = false;
        vm.datePickerOpenStatus.statWeek = false;
        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
        
        var unsubscribe = $rootScope.$on('cpmApp:deptInfoSelected', function(event, result) {
        	vm.searchQuery.userId = result.objId;
        	vm.searchQuery.userName = result.name;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
