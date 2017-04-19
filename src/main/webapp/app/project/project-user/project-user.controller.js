(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ProjectUserController', ProjectUserController);

    ProjectUserController.$inject = ['$scope','$rootScope', '$state', 'ProjectUser', 'ParseLinks', 'AlertService', 'paginationConstants', 'pagingParams','ProjectInfo'];

    function ProjectUserController ($scope,$rootScope, $state, ProjectUser, ParseLinks, AlertService, paginationConstants, pagingParams,ProjectInfo) {
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
        vm.searchQuery.projectId = pagingParams.projectId;
        vm.searchQuery.userId = pagingParams.userId;
        vm.searchQuery.userName = pagingParams.userName;
        
        vm.exportXls = exportXls;
        
        if (!vm.searchQuery.projectId && !vm.searchQuery.userId){
        	vm.haveSearch = null;
        }else{
        	vm.haveSearch = true;
        }
        //加载搜索下拉框
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
        //加载列表页
        loadAll();
        function loadAll () {
        	if(pagingParams.projectId == undefined){
        		pagingParams.projectId = "";
        	}
			if(pagingParams.userId == undefined){
				pagingParams.userId = "";
			}
            ProjectUser.query({
            	projectId:pagingParams.projectId,
            	userId:pagingParams.userId,            	
                page: pagingParams.page - 1,
                size: vm.itemsPerPage,
                sort: sort()
            }, onSuccess, onError);
            
            function sort() {
                var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
                if (vm.predicate !== 'wpu.id') {
                    result.push('wpu.id');
                }
                return result;
            }
            function onSuccess(data, headers) {
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
                vm.queryCount = vm.totalItems;
                vm.projectUsers = data;
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
                projectId:vm.searchQuery.projectId ? vm.searchQuery.projectId.key : "",
                userId:vm.searchQuery.userId,
                userName:vm.searchQuery.userName,
            });
        }

        function search() {
            if (!vm.searchQuery.projectId && !vm.searchQuery.userId){
                return vm.clear();
            }
            vm.links = null;
            vm.page = 1;
            vm.predicate = 'wpu.id';
            vm.reverse = false;
            vm.haveSearch = true;
            vm.transition();
        }

        function exportXls(){
        	var url = "api/project-user/exportXls";
        	var c = 0;
        	var projectId = vm.searchQuery.projectId && vm.searchQuery.projectId.key? vm.searchQuery.projectId.key : vm.searchQuery.projectId;
        	var userId = vm.searchQuery.userId;
			if(projectId){
				if(c == 0){
					c++;
					url += "?";
				}else{
					url += "&";
				}
				url += "projectId="+encodeURI(projectId);
			}
			if(userId){
				if(c == 0){
					c++;
					url += "?";
				}else{
					url += "&";
				}
				url += "userId="+encodeURI(userId);
			}
        	window.open(url);
        }
        
        function clear() {
            vm.links = null;
            vm.page = 1;
            vm.predicate = 'wpu.id';
            vm.reverse = false;
            vm.searchQuery = {};
            vm.haveSearch = null;
            vm.transition();
        }
        
        var unsubscribe = $rootScope.$on('cpmApp:deptInfoSelected', function(event, result) {
        	vm.searchQuery.userId = result.objId;
        	vm.searchQuery.userName = result.name;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
