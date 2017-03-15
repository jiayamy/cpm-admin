(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ContractBudgetController', ContractBudgetController);

    ContractBudgetController.$inject = ['$scope', '$state', 'ContractBudget', 'ParseLinks', 'AlertService', 'paginationConstants', 'pagingParams'];

    function ContractBudgetController ($scope, $state, ContractBudget, ParseLinks, AlertService, paginationConstants, pagingParams) {
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
        //搜索中的参数
        vm.purchaseTypes = [{id: 1,name: "硬件"},{id: 2,name: "软件"},{id: 3,name: "服务"}];
        for (var i = 0; i < vm.purchaseTypes.length; i++) {
			if (pagingParams.purchaseType == vm.purchaseTypes[i].id) {
				vm.searchQuery.purchaseType = vm.purchaseTypes[i];
			}
		}
        
        vm.searchQuery.contractId = pagingParams.contractId;
        vm.searchQuery.name = pagingParams.name;
        
        if (!vm.searchQuery.contractId && !vm.searchQuery.name && !vm.searchQuery.purchaseType){
        	vm.haveSearch = null;
        }else{
        	vm.haveSearch = true;
        }
        vm.contractInfos = [];
        loadContract();
        function loadContract(){
        	ContractBudget.queryUserContract({
        		
        	},
        	function(data, headers){
        		vm.contractInfos = data;
        		if(vm.contractInfos && vm.contractInfos.length > 0){
        			for(var i = 0; i < vm.contractInfos.length; i++){
        				if(pagingParams.contractId == vm.contractInfos[i].key){
        					vm.searchQuery.contractId = vm.contractInfos[i];
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
            	if(pagingParams.contractId == undefined){
            		pagingParams.contractId = "";
            	}
            	if(pagingParams.name == undefined){
            		pagingParams.name = "";
            	}
            	if (pagingParams.purchaseType == undefined) {
					pagingParams.purchaseType = "";
				}
            	ContractBudget.query({
            		name: pagingParams.name,
            		contractId: pagingParams.contractId,
            		purchaseType: pagingParams.purchaseType,
                    page: pagingParams.page - 1,
                    size: vm.itemsPerPage,
                    sort: sort()
                }, onSuccess, onError);
            function sort() {
                var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
                if (vm.predicate !== 'wcb.id') {
                    result.push('wcb.id');
                }
                return result;
            }
            function onSuccess(data, headers) {
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
                vm.queryCount = vm.totalItems;
                vm.contractBudgets = handleData(data);
                vm.page = pagingParams.page;
            }
            function onError(error) {
                AlertService.error(error.data.message);
            }
        }
        function handleData(data){
        	if (data.length > 0) {
				for(var i = 0; i< data.length ; i++){
					if (data[i].status == 1) {
						data[i].statusName = "可用";
					}else if (data[i].status == 2) {
						data[i].statusName = "删除";
					}
					if (data[i].purchaseType == 1) {
						data[i].purchaseTypeName = "硬件";
					}else if (data[i].purchaseType == 2) {
						data[i].purchaseTypeName = "软件";
					}else if (data[i].purchaseType == 3) {
						data[i].purchaseTypeName = "服务";
					}
					if (data[i].isEdit == true) {
						data[i].haveEdit = true;
					}else if (data[i].isEdit == false) {
						data[i].haveEdit = null;
					}
					if (data[i].isCreate == true) {
						if (data[i].purchaseType == 3) {
							if (data[i].status == 1 && data[i].hasCreatedProject == true && data[i].isValidable == true) {
								data[i].haveCreateProject = true;
								data[i].isCreateDisabled = false;
							}else{
								data[i].haveCreateProject = true;
								data[i].isCreateDisabled = true;
							}
							data[i].haveCreateItem = null;
						}else {
							if (data[i].status == 1 && data[i].isValidable == true) {
								data[i].haveCreateItem = true;
								data[i].isCreateDisabled = false;
							}else{
								data[i].haveCreateItem = true;
								data[i].isCreateDisabled = true;
							}
							data[i].haveCreateProject = null;
						}
					}else{
						data[i].haveCreateProject = null;
						data[i].haveCreateItem = null;
						data[i].isCreateDisabled = true;
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
                contractId: vm.searchQuery.contractId ? vm.searchQuery.contractId.key: "",
                name:vm.searchQuery.name,
                purchaseType:vm.searchQuery.purchaseType ? vm.searchQuery.purchaseType.id : ""
            });
        }

        function search() {
            if (!vm.searchQuery.contractId && !vm.searchQuery.name && !vm.searchQuery.purchaseType){
                return vm.clear();
            }
            vm.links = null;
            vm.page = 1;
            vm.predicate = 'wcb.id';
            vm.reverse = false;
            vm.haveSearch = true;
            vm.transition();
        }

        function clear() {
            vm.links = null;
            vm.page = 1;
            vm.predicate = 'wcb.id';
            vm.reverse = true;
            vm.searchQuery = {};
            vm.haveSearch = null;
            vm.transition();
        }
    }
})();
