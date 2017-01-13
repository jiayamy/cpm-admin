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
        var purchaseType = pagingParams.purchaseType;
        if (purchaseType) {
			if (purchaseType == 1) {
				purchaseType = { id: 1, name: '硬件'};
			}else if (purchaseType == 2) {
				purchaseType = { id: 2, name: '软件'};
			}else if (purchaseType == 3) {
				purchaseType = { id: 3, name: '服务'};
			}
		}
        vm.searchQuery.purchaseType = pagingParams.purchaseType;
        vm.searchQuery.serialNum = pagingParams.serialNum;
        vm.searchQuery.name = pagingParams.name;
        vm.searchQuery.budgetName = pagingParams.budgetName;
        vm.currentSearch = {};
        vm.currentSearch.purchaseType = pagingParams.purchaseType;
        vm.currentSearch.serialNum = pagingParams.serialNum;
        vm.currentSearch.name = pagingParams.name;
        vm.currentSearch.budgetName = pagingParams.budgetName;
        if (!vm.currentSearch.serialNum && !vm.currentSearch.name && !vm.currentSearch.budgetName){
        	vm.currentSearch.haveSearch = null;
        }else{
        	vm.currentSearch.haveSearch = true;
        }
        vm.purchaseTypes = [{ id: 1, name: '硬件' }, { id: 2, name: '软件' }, { id: 3, name: '服务'}];
        loadAll();

        function loadAll () {
            	if(pagingParams.serialNum == undefined){
            		pagingParams.serialNum = "";
            	}
            	if(pagingParams.name == undefined){
            		pagingParams.name = "";
            	}
            	if (pagingParams.budgetName == undefined) {
					pagingParams.budgetName = "";
				}
            	ContractBudget.query({
            		name: pagingParams.name,
            		serialNum: pagingParams.serialNum,
            		budgetName: pagingParams.budgetName,
                    page: pagingParams.page - 1,
                    size: vm.itemsPerPage,
                    sort: sort()
                }, onSuccess, onError);
            function sort() {
                var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
                if (vm.predicate !== 'cb.id') {
                    result.push('cb.id');
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
						data[i].status = "可用";
					}else if (data[i].status == 2) {
						data[i].status = "删除";
					}
					if (data[i].purchaseType == 1) {
						data[i].purchaseType = "硬件";
					}else if (data[i].purchaseType == 2) {
						data[i].purchaseType = "软件";
					}else if (data[i].purchaseType == 3) {
						data[i].purchaseType = "服务";
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
                serialNum:vm.currentSearch.serialNum,
                name:vm.currentSearch.name,
                budgetName:vm.currentSearch.budgetName
            });
        }

        function search(searchQuery) {
            if (!searchQuery.serialNum && !searchQuery.name && !searchQuery.budgetName){
                return vm.clear();
            }
            vm.links = null;
            vm.page = 1;
            vm.predicate = 'cb.id';
            vm.reverse = false;
            vm.currentSearch = searchQuery;
            vm.currentSearch.haveSearch = true;
            vm.transition();
        }

        function clear() {
            vm.links = null;
            vm.page = 1;
            vm.predicate = 'cb.id';
            vm.reverse = true;
            vm.currentSearch = {};
            vm.currentSearch.haveSearch = null;
            vm.transition();
        }
    }
})();
