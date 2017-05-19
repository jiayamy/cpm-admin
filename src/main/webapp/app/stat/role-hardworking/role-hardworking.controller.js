(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('RoleHardWorkingController', RoleHardWorkingController);

    RoleHardWorkingController.$inject = ['$scope','$rootScope', '$state', 'RoleHardWorking', 'ParseLinks', 'AlertService', 'paginationConstants', 'pagingParams','DateUtils'];

    function RoleHardWorkingController ($scope,$rootScope, $state, RoleHardWorking, ParseLinks, AlertService, paginationConstants, pagingParams,DateUtils) {
    	var vm = this;
        vm.predicate = pagingParams.predicate;
        vm.reverse = pagingParams.ascending;
        vm.transition = transition;
        vm.itemsPerPage = paginationConstants.itemsPerPage;
        vm.clear = clear;
        vm.search = search;
        vm.searchQuery = {};
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        var date = pagingParams.originMonth;
        
        
        var today = new Date();
        if(!date){
        	date = DateUtils.convertLocalDateToFormat(new Date(),"yyyyMM");
        	date = new Date(date.substring(0,4),parseInt(date.substring(4,6))-1,0);
        }
        
      //搜索中的参数
        if(date && date.length == 6){
        	date = new Date(date.substring(0,4),parseInt(date.substring(4,6)),0);
        }
        vm.searchQuery.originMonth = date;
        vm.searchQuery.userId = pagingParams.userId;
        vm.searchQuery.userName = pagingParams.userName;
        if (!vm.searchQuery.originMonth && !vm.searchQuery.userId && !vm.searchQuery.userName){
        	vm.haveSearch = null;
        }else{
        	vm.haveSearch = true;
        }
        //加载列表信息
        loadAll();
        function loadAll () {
        	if (pagingParams.userId == undefined) {
				pagingParams.userId = "";
			}
        	RoleHardWorking.query({
        		originMonth: pagingParams.originMonth,
        		userId: pagingParams.userId,
                page: pagingParams.page - 1,
                size: vm.itemsPerPage,
                sort: sort()
            }, onSuccess, onError);
        	

        	
        	function sort() {
                var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
                if (vm.predicate !== 'rhw.id') {
                    result.push('rhw.id');
                }
                return result;
            }
        	function onSuccess(data, headers) {
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
                vm.queryCount = vm.totalItems;
                vm.roleHardWorking = handleData(data);
                vm.page = pagingParams.page;
            }
        	
        	function onError(error) {
                AlertService.error(error.data.message);
            }
        }
        function handleData(data){
        	return data;
        }
        
        function transition() {
            $state.transitionTo($state.$current, {
                page: vm.page,
                sort: vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc'),
                originMonth: DateUtils.convertLocalDateToFormat(vm.searchQuery.originMonth,"yyyyMM"),
                userId:vm.searchQuery.userId,
                userName: vm.searchQuery.userName
            });
        }
        
        function search() {
            if (!vm.searchQuery.originMonth && !vm.searchQuery.userId){
                return vm.clear();
            }
            vm.links = null;
            vm.page = 1;
            vm.predicate = 'rhw.id';
            vm.reverse = false;
            vm.haveSearch = true;
            vm.transition();
        }
        
        function clear() {
        	var day = new Date();
            if(date){
            	date = DateUtils.convertLocalDateToFormat(day,"yyyyMM");
            	date = new Date(date.substring(0,4),parseInt(date.substring(4,6))-1,0);
            }
            vm.searchQuery.originMonth = date;
            vm.searchQuery.userId = null;
            vm.searchQuery.userName = null;
            vm.links = null;
            vm.page = 1;
            vm.predicate = 'rhw.id';
            vm.transition();
        }
        
        
        vm.datePickerOpenStatus.originMonth = false;
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
