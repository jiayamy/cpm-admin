(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ContractReceiveController', ContractReceiveController);

    ContractReceiveController.$inject = ['$scope', '$state', 'ContractReceive', 'ParseLinks', 'AlertService', 'paginationConstants', 'pagingParams','ContractInfo'];

    function ContractReceiveController ($scope, $state, ContractReceive, ParseLinks, AlertService, paginationConstants, pagingParams,ContractInfo) {
        var vm = this;

        vm.loadPage = loadPage;
        vm.predicate = pagingParams.predicate;
        vm.reverse = pagingParams.ascending;
        vm.transition = transition;
        vm.itemsPerPage = paginationConstants.itemsPerPage;
        vm.clear = clear;
        vm.search = search;
        vm.loadAll = loadAll;
        
        vm.statuss = [{key:1,val:'可用'},{key:2,val:'删除'}];

        vm.searchQuery ={};
        vm.searchQuery.contractId = pagingParams.contractId;
        if (!vm.searchQuery.contractId){
        	vm.haveSearch = null;
        }else{
        	vm.haveSearch = true;
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
            ContractReceive.query({
            	contractId:pagingParams.contractId,
                page: pagingParams.page - 1,
                size: vm.itemsPerPage,
                sort: sort()
            }, onSuccess, onError);
            function sort() {
                var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
                if (vm.predicate !== 'wcr.id') {
                    result.push('wcr.id');
                }
                return result;
            }
            function onSuccess(data, headers) {
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
                vm.queryCount = vm.totalItems;
                vm.contractReceives = handleData(data);
                vm.page = pagingParams.page;
            }
            function onError(error) {
                AlertService.error(error.data.message);
            }
            //处理status 的显示
            function handleData(data){
            	if(data && data.length > 0){
            		for(var i = 0; i < data.length; i++){
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
                contractId:vm.searchQuery.contractId ? vm.searchQuery.contractId.key : null,
            });
        }

        function search() {
            if (vm.searchQuery.contractId == undefined){
                return vm.clear();
            }
            vm.links = null;
            vm.page = 1;
            vm.predicate = 'wcr.id';
            vm.reverse = false;
            vm.haveSearch = true;
            vm.transition();
        }

        function clear() {
            vm.links = null;
            vm.page = 1;
            vm.predicate = 'wcr.id';
            vm.reverse = false;
            vm.haveSearch = null;
            vm.searchQuery = {};
            vm.transition();
        }
    }
})();
