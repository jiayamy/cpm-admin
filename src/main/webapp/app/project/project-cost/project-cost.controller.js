(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ProjectCostController', ProjectCostController);

    ProjectCostController.$inject = ['$scope', '$state', 'ProjectCost', 'ParseLinks', 'AlertService','ProjectInfo','pageType', 'paginationConstants', 'pagingParams'];

    function ProjectCostController ($scope, $state, ProjectCost, ParseLinks, AlertService,ProjectInfo,pageType, paginationConstants, pagingParams) {
        var vm = this;
        vm.loadPage = loadPage;
        vm.predicate = pagingParams.predicate;
        vm.reverse = pagingParams.ascending;
        vm.transition = transition;
        vm.itemsPerPage = paginationConstants.itemsPerPage;
        vm.clear = clear;
        vm.search = search;
        vm.loadAll = loadAll;
        vm.isShow = false;
        
        if(pageType == 1){
        	vm.types = [{key:1,val:'工时'}];
        	vm.viewUiSref = "project-cost-timesheet-detail";
        	vm.canEdit = false;
        	vm.projectCostTitle = "cpmApp.projectCost.home.timesheetTitle";
        	vm.isShow = true;
        }else{
        	vm.types = [{key:2,val:'差旅'},{key:3,val:'采购'},{key:4,val:'商务'}];
        	vm.viewUiSref = "project-cost-detail";
        	vm.canEdit = true;
        	vm.projectCostTitle = "cpmApp.projectCost.home.title";
        }
        vm.statuss = [{key:1,val:'正常'},{key:2,val:'删除'}];
        vm.searchQuery = {};
        vm.searchQuery.projectId = pagingParams.projectId;
        vm.searchQuery.type = pagingParams.type;
        vm.searchQuery.name = pagingParams.name;
        
        if (!vm.searchQuery.projectId && !vm.searchQuery.type && !vm.searchQuery.name){
        	vm.haveSearch = null;
        }else{
        	vm.haveSearch = true;
        }
        for(var i = 0; i < vm.types.length; i++){
        	if(pagingParams.type == vm.types[i].key){
        		vm.searchQuery.type = vm.types[i];
        	}
        }
        vm.projectInfos = [];
        loadProjectInfos();
        function loadProjectInfos(){
        	ProjectInfo.queryProjectInfo(
        		{
        			
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
        		}
        	);
        }
        
        loadAll();
        function loadAll () {
        	if(pagingParams.projectId == undefined){
        		pagingParams.projectId = "";
        	}
        	if(pagingParams.type == undefined){
        		pagingParams.type = "";
        	}
        	if(pagingParams.name == undefined){
        		pagingParams.name = "";
        	}
            ProjectCost.query({
            	projectId: pagingParams.projectId,
               	type: pagingParams.type,
                name: pagingParams.name,
                pageType:pageType,
                page: pagingParams.page - 1,
                size: vm.itemsPerPage,
                sort: sort()
            }, onSuccess, onError);
            function sort() {
                var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
                if (vm.predicate !== 'wpc.id') {
                    result.push('wpc.id');
                }
                return result;
            }
            function onSuccess(data, headers) {
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
                vm.queryCount = vm.totalItems;
                vm.projectCosts = handleData(data);
                vm.page = pagingParams.page;
            }
            function onError(error) {
                AlertService.error(error.data.message);
            }
            function handleData(data){
            	if(data.length > 0){
            		for(var i = 0; i< data.length ; i++){
            			for(var j = 0; j < vm.types.length; j++){
            	        	if(data[i].type == vm.types[j].key){
            	        		data[i].typeName = vm.types[j].val;
            	        	}
            	        }
            			for(var j = 0; j < vm.statuss.length; j++){
            	        	if(data[i].status == vm.statuss[j].key){
            	        		data[i].statusName = vm.statuss[j].val;
            	        	}
            	        }
            		}
            	}
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
                projectId:vm.searchQuery.projectId ? vm.searchQuery.projectId.key : "",
                type:vm.searchQuery.type ? vm.searchQuery.type.key : "",
                name:vm.searchQuery.name,
            });
        }

        function search() {
        	if (!vm.searchQuery.projectId 
        			&& !vm.searchQuery.type 
        			&& !vm.searchQuery.name){
                return vm.clear();
            }
            vm.links = null;
            vm.page = 1;
            vm.predicate = 'wpc.costDay';
            vm.reverse = false;
            vm.haveSearch = true;
            vm.transition();
        }

        function clear() {
            vm.links = null;
            vm.page = 1;
            vm.predicate = 'wpc.costDay';
            vm.reverse = false;
            vm.searchQuery = {};
            vm.haveSearch = null;
            vm.transition();
        }
    }
})();
