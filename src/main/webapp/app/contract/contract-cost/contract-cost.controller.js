(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ContractCostController', ContractCostController);

    ContractCostController.$inject = ['ContractInfo','$scope', '$state', 'ContractCost', 'ContractCostSearch', 'ParseLinks', 'AlertService', 'paginationConstants', 'pagingParams'];

    function ContractCostController (ContractInfo,$scope, $state, ContractCost, ContractCostSearch, ParseLinks, AlertService, paginationConstants, pagingParams) {
        var vm = this;

        vm.loadPage = loadPage;
        vm.predicate = pagingParams.predicate;
        vm.reverse = pagingParams.ascending;
        vm.transition = transition;
        vm.itemsPerPage = paginationConstants.itemsPerPage;
        vm.clear = clear;
        vm.search = search;
        vm.loadAll = loadAll;
        
        //枚举值
        vm.types = [{key:1,val:'工时'},{key:2,val:'差旅'},{key:3,val:'采购'},{key:4,val:'商务'}];
        vm.statuss = [{key:1,val:'可用'},{key:2,val:'删除'}];
        vm.searchQuery = {};
        
        vm.searchQuery.contractId = pagingParams.contractId;
        vm.searchQuery.type = pagingParams.type;
        vm.searchQuery.name = pagingParams.name;
        
        if (!vm.searchQuery.contractId && !vm.searchQuery.type && !vm.searchQuery.name){
        	vm.haveSearch = null;
        }else{
        	vm.haveSearch = true;
        }
        //回显type
        for(var i = 0; i < vm.types.length; i++){
        	if(pagingParams.type == vm.types[i].key){
        		vm.searchQuery.type = vm.types[i];
        	}
        }
        vm.contractInfos = [];
        loadContractInfos();
        loadAll();
        
        function loadContractInfos(){
        	ContractInfo.queryContractInfo(
        		{
        			
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
        		}
        	);
        }
        
        
        function loadAll () {
        	if(pagingParams.contractId == undefined){
        		pagingParams.contractId = "";
        	}
        	if(pagingParams.type == undefined){
        		pagingParams.type = "";
        	}
        	if(pagingParams.name == undefined){
        		pagingParams.name = "";
        	}
        	
        	
            ContractCost.query({
            	contractId: pagingParams.contractId,
               	type: pagingParams.type,
                name: pagingParams.name,
                page: pagingParams.page - 1,
                size: vm.itemsPerPage,
                sort: sort()
            }, onSuccess, onError);
            function sort() {
                var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
                if (vm.predicate !== 'wcc.id') {
                    result.push('wcc.id');
                }
                return result;
            }
            function onSuccess(data, headers) {
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
                vm.queryCount = vm.totalItems;
                vm.contractCosts = handleData(data);
                vm.page = pagingParams.page;
            }
            function onError(error) {
                AlertService.error(error.data.message);
            }
            //处理type status 的显示问题
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
                contractId:vm.searchQuery.contractId ? vm.searchQuery.contractId.key : "",
                type:vm.searchQuery.type ? vm.searchQuery.type.key : "",
                name:vm.searchQuery.name,
            });
        }

        function search() {
        	if (!vm.searchQuery.contractId 
        			&& !vm.searchQuery.type 
        			&& !vm.searchQuery.name){
                return vm.clear();
            }
            vm.links = null;
            vm.page = 1;
            vm.predicate = 'wcc.id';
            vm.reverse = false;
            vm.haveSearch = true;
            vm.transition();
        }

        function clear() {
            vm.links = null;
            vm.page = 1;
            vm.predicate = 'wcc.id';
            vm.reverse = true;
            vm.searchQuery = {};
            vm.haveSearch = null;
            vm.transition();
        }
    }
})();
