(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ContractUserController', ContractUserController);

    ContractUserController.$inject = ['ContractInfo','$rootScope','$scope', '$state', 'ContractUser', 'ParseLinks', 'AlertService', 'paginationConstants', 'pagingParams'];

    function ContractUserController (ContractInfo,$rootScope,$scope, $state, ContractUser, ParseLinks, AlertService, paginationConstants, pagingParams) {
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
        vm.searchQuery.contractId = pagingParams.contractId;
        vm.searchQuery.userId = pagingParams.userId;
        vm.searchQuery.userName = pagingParams.userName;
        vm.exportXls = exportXls;
        if (!vm.searchQuery.contractId && !vm.searchQuery.userId){
        	vm.haveSearch = null;
        }else{
        	vm.haveSearch = true;
        }
        //加载搜索下拉框
        vm.contractInfos = [];
        loadContractInfos();
       
        //加载列表页
        loadAll();
        function loadContractInfos(){
        	ContractInfo.queryContractInfo(
    			{
        			
        		},
        		function(data,headers){
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
        		}
        	
        	);
        }
        function loadAll () {
        	if(pagingParams.contractId == undefined){
        		pagingParams.contractId = "";
        	}
			if(pagingParams.userId == undefined){
				pagingParams.userId = "";
			}
        	ContractUser.query({
        		contractId:pagingParams.contractId,
            	userId:pagingParams.userId,
	            page: pagingParams.page - 1,
	            size: vm.itemsPerPage,
	            sort: sort()
        	}, onSuccess, onError);
            function sort() {
                var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
                if (vm.predicate !== 'wcu.id') {
                    result.push('wcu.id');
                }
                return result;
            }
            function onSuccess(data, headers) {
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
                vm.queryCount = vm.totalItems;
                vm.contractUsers = data;
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
                contractId: vm.searchQuery.contractId ? vm.searchQuery.contractId.key : "",
                userId: vm.searchQuery.userId,
                userName: vm.searchQuery.userName,
            });
        }

        function search() {
        	 if (!vm.searchQuery.contractId && !vm.searchQuery.userId){
                 return vm.clear();
             }
            vm.links = null;
            vm.page = 1;
            vm.predicate = 'wcu.id';
            vm.reverse = false;
            vm.haveSearch = true;
            vm.transition();
        }
        
        function exportXls(){
        	var url = "api/contract-users/exportXls";
        	var c = 0;
        	var contractId = vm.searchQuery.contractId && vm.searchQuery.contractId.key? vm.searchQuery.contractId.key : vm.searchQuery.contractId;
        	var userId = vm.searchQuery.userId;
			if(contractId){
				if(c == 0){
					c++;
					url += "?";
				}else{
					url += "&";
				}
				url += "contractId="+encodeURI(contractId);
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
            vm.predicate = 'wcu.id';
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
