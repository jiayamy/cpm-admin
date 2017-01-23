(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ProjectWeeklyStatController', ProjectWeeklyStatController);

    ProjectWeeklyStatController.$inject = ['ProjectInfo','$scope', '$state', 'DateUtils','ProjectWeeklyStat', 'ParseLinks', 'AlertService', 'paginationConstants', 'pagingParams'];

    function ProjectWeeklyStatController (ProjectInfo,$scope, $state,DateUtils,  ProjectWeeklyStat, ParseLinks, AlertService, paginationConstants, pagingParams) {
    	var vm = this;

        vm.loadPage = loadPage;
        vm.predicate = pagingParams.predicate;
        vm.reverse = pagingParams.ascending;
        vm.transition = transition;
        vm.itemsPerPage = 10;
        vm.clear = clear;
        vm.loadAll = loadAll;
        vm.search = search;
        vm.searchQuery = {};
        vm.searchQuery.projectId= pagingParams.projectId;
        vm.projectInfos = [];
        if (!vm.searchQuery.projectId){
        	vm.haveSearch = null;
        }else{
        	vm.haveSearch = true;
        }
        loadProject();
        function loadProject(){
        	ProjectInfo.queryProjectInfo({
        		
        	},
        	function(data, headers){
        		vm.projectInfos = data;
        		if(vm.projectInfos && vm.projectInfos.length > 0){
        			for(var i = 0; i < vm.projectInfos.length; i++){
        				if(pagingParams.projectId == vm.projectInfos[i].key){
        					vm.searchQuery.projectId = vm.projectInfos[i];
        				}
        			}
        		}
        	},
        	function(error){
        		AlertService.error(error.data.message);
        	});
        }
        
        loadAll();

        function loadAll () {
        	if(pagingParams.projectId == undefined){
        		pagingParams.projectId = "";
        	}
        	ProjectWeeklyStat.query({
                page: pagingParams.page - 1,
                size: vm.itemsPerPage,
                sort: sort(),
                projectId : pagingParams.projectId
            }, onSuccess, onError);
           
            function sort() {
                var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
                if (vm.predicate !== 'm.id') {
                    result.push('m.id');
                }
                return result;
            }
            function onSuccess(data, headers) {
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
                vm.queryCount = vm.totalItems;
                vm.projectWeeklyStats = data;
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
                projectId:vm.searchQuery.projectId ? vm.searchQuery.projectId.key : ""
            });
        }

        
        function clear() {
            vm.links = null;
            vm.page = 1;
            vm.predicate = 'm.id';
            vm.reverse = true;
            vm.searchQuery = {};
            vm.haveSearch = null;
            vm.transition();
        }
        function search(searchQuery) {
        	if (!vm.searchQuery.projectId){
                return vm.clear();
            }
            vm.links = null;
            vm.page = 1;
            vm.predicate = 'm.id';
            vm.reverse = false;
            vm.haveSearch = true;
            vm.transition();
        }
    }
})();
