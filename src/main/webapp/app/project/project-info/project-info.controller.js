(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ProjectInfoController', ProjectInfoController);

    ProjectInfoController.$inject = ['$scope', '$state', 'ProjectInfo', 'ParseLinks', 'AlertService', 'paginationConstants', 'pagingParams'];

    function ProjectInfoController ($scope, $state, ProjectInfo, ParseLinks, AlertService, paginationConstants, pagingParams) {
        var vm = this;
        vm.loadPage = loadPage;
        vm.predicate = pagingParams.predicate;
        vm.reverse = pagingParams.ascending;
        vm.transition = transition;
        vm.itemsPerPage = paginationConstants.itemsPerPage;
        vm.search = search;
        vm.clear = clear;
        vm.exportXls = exportXls;
        
        vm.searchQuery = {};
        //搜索项中的参数
        vm.prjStatuss = [{key:1,val:"开发中"},{key:2,val:"已结项"},{key:3,val:"已终止"}]
        for(var i = 0; i < vm.prjStatuss.length; i++){
			if(pagingParams.status == vm.prjStatuss[i].key){
				vm.searchQuery.status = vm.prjStatuss[i];
			}
		}
        vm.searchQuery.serialNum = pagingParams.serialNum;
        vm.searchQuery.name = pagingParams.name;
        vm.searchQuery.contractId = pagingParams.contractId;
        vm.contractInfos = [];
        
        if (!vm.searchQuery.contractId && !vm.searchQuery.serialNum
        		&& !vm.searchQuery.name && !vm.searchQuery.status){
        	vm.haveSearch = null;
        }else{
        	vm.haveSearch = true;
        }
        
        loadContract();
        function loadContract(){
        	ProjectInfo.queryUserContract({
        		
        	},
        	function(data, headers){
        		vm.contractInfos = data;
        		if(vm.contractInfos && vm.contractInfos.length > 0){
        			for(var i = 0; i < vm.contractInfos.length; i++){
        				if(pagingParams.contractId == vm.contractInfos[i].key){
        					vm.searchQuery.contractId = vm.contractInfos[i];
        					angular.element('select[ng-model="vm.searchQuery.contractId"]').parent().find(".select2-chosen").html(vm.contractInfos[i].val);
        				}
        			}
        		}
        	},
        	function(error){
        		AlertService.error(error.data.message);
        	});
        }
        //加载列表数据
        loadAll();
        function loadAll () {
        	if(pagingParams.contractId == undefined){
        		pagingParams.contractId = "";
        	}
			if(pagingParams.serialNum == undefined){
				pagingParams.serialNum = "";
			}
			if(pagingParams.name == undefined){
				pagingParams.name = "";
			}
			if(pagingParams.status == undefined){
				pagingParams.status = "";
			}
            ProjectInfo.query({
            	contractId:pagingParams.contractId,
            	serialNum:pagingParams.serialNum,
            	name:pagingParams.name,
            	status:pagingParams.status,
                page: pagingParams.page - 1,
                size: vm.itemsPerPage,
                sort: sort()
            }, onSuccess, onError);
            function sort() {
                var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
                if (vm.predicate !== 'wpi.id') {
                    result.push('wpi.id');
                }
                return result;
            }
            function onSuccess(data, headers) {
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
                vm.queryCount = vm.totalItems;
                vm.projectInfos = handleData(data);
                vm.page = pagingParams.page;
            }
            function onError(error) {
                AlertService.error(error.data.message);
            }
            function handleData(data){
            	if(data.length > 0){
            		for(var i = 0; i< data.length ; i++){
            			if(data[i].status == 1){
            				data[i].status = "开发中";
            			}else if(data[i].status == 2){
            				data[i].status = "已结项";
            			}else if(data[i].status == 3){
            				data[i].status = "已终止";
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
                contractId:vm.searchQuery.contractId ? vm.searchQuery.contractId.key : "",
            	serialNum:vm.searchQuery.serialNum,
            	name:vm.searchQuery.name,
            	status:vm.searchQuery.status ? vm.searchQuery.status.key : ""
            });
        }

        function search() {
            if (!vm.searchQuery.contractId && !vm.searchQuery.serialNum
            		&& !vm.searchQuery.name && !vm.searchQuery.status){
                return vm.clear();
            }
            vm.links = null;
            vm.page = 1;
            vm.predicate = 'wpi.id';
            vm.reverse = false;
            vm.haveSearch = true;
            vm.transition();
        }

        function clear() {
            vm.links = null;
            vm.page = 1;
            vm.predicate = 'wpi.id';
            vm.reverse = false;
            vm.searchQuery = {};
            vm.haveSearch = null;
            vm.transition();
        }
        
        function exportXls(){//导出Xls
        	var url = "api/project-infos/exportXls";
        	var c = 0;
    		
    		var contractId = vm.searchQuery.contractId ? vm.searchQuery.contractId.key : "";
        	var serialNum = vm.searchQuery.serialNum;
        	var name = vm.searchQuery.name;
        	var status = vm.searchQuery.status ? vm.searchQuery.status.key : "";
			
			if(contractId){
				if(c == 0){
					c++;
					url += "?";
				}else{
					url += "&";
				}
				url += "contractId="+encodeURI(contractId);
			}
			if(serialNum){
				if(c == 0){
					c++;
					url += "?";
				}else{
					url += "&";
				}
				url += "serialNum="+encodeURI(serialNum);
			}
			if(name){
				if(c == 0){
					c++;
					url += "?";
				}else{
					url += "&";
				}
				url += "name="+encodeURI(name);
			}
			if(status){
				if(c == 0){
					c++;
					url += "?";
				}else{
					url += "&";
				}
				url += "status="+encodeURI(status);
			}
			
        	window.open(url);
        }
    }
})();
