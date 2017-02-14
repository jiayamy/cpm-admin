(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('PurchaseItemController', PurchaseItemController);

    PurchaseItemController.$inject = ['$scope', '$state', 'PurchaseItem', 'ParseLinks', 'AlertService', 'paginationConstants', 'pagingParams'];

    function PurchaseItemController ($scope, $state, PurchaseItem, ParseLinks, AlertService, paginationConstants, pagingParams) {
        var vm = this;

        vm.loadPage = loadPage;
        vm.predicate = pagingParams.predicate;
        vm.reverse = pagingParams.ascending;
        vm.transition = transition;
        vm.itemsPerPage = paginationConstants.itemsPerPage;
        vm.clear = clear;
        vm.search = search;
        vm.searchQuery = {};
        //搜索中的参数
        vm.sources = [{id: 1,name: "内部采购"},{id: 2, name: "外部采购"}];
        for (var i = 0; i < vm.sources.length; i++) {
			if (pagingParams.source == vm.sources[i].id) {
				vm.searchQuery.source = vm.sources[i];
			}
		}
        vm.types = [{id: 1, name: "硬件"},{id: 2, name: "软件"}];
        for (var i = 0; i < vm.types.length; i++){
        	if (pagingParams.ppType == vm.types[i].id) {
        		vm.searchQuery.type = vm.types[i];
			}
        }
        vm.searchQuery.contractId = pagingParams.contractId;
        vm.searchQuery.name = pagingParams.name;
        
        if (!vm.searchQuery.name && !vm.searchQuery.contractId
        		&& !vm.searchQuery.source && !vm.searchQuery.type) {
			vm.haveSearch = null;
		}else {
			vm.haveSearch = true;
		}
        vm.contractInfos = [];
        loadContract();
        function loadContract(){
        	PurchaseItem.queryUserContract({
        		
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
        //加载列表信息
        loadAll();
        function loadAll () {
        	if (pagingParams.name == undefined) {
        		pagingParams.name = "";
			}
        	if (pagingParams.contractId == undefined) {
        		pagingParams.contractId = "";
			}
        	if (pagingParams.source == undefined) {
        		pagingParams.source = "";
			}
        	if (pagingParams.ppType == undefined) {
        		pagingParams.ppType = "";
			}
            PurchaseItem.query({
            	name:pagingParams.name,
            	contractId:pagingParams.contractId,
            	source:pagingParams.source,
            	ppType:pagingParams.ppType,
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
                vm.purchaseItems = handleData(data);
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
						data[i].statusName = "已删除";
					}
					if (data[i].type == 1) {
						data[i].typeName = "硬件";
					}else if (data[i].type == 2) {
						data[i].typeName = "软件";
					}
					if (data[i].source == 1) {
						data[i].sourceName = "内部";
					}else if (data[i].source == 2) {
						data[i].sourceName = "外部";
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
                name: vm.searchQuery.name,
                contractId: vm.searchQuery.contractId ? vm.searchQuery.contractId.key: "",
                source: vm.searchQuery.source ? vm.searchQuery.source.id : "",
                ppType: vm.searchQuery.type ? vm.searchQuery.type.id : ""
            });
        }

        function search() {
            if (!vm.searchQuery.name && !vm.searchQuery.contractId &&
            		!vm.searchQuery.source && !vm.searchQuery.type){
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
    }
})();
