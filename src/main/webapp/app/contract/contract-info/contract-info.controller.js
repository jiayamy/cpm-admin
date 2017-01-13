(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ContractInfoController', ContractInfoController);

    ContractInfoController.$inject = ['$rootScope','$http','$scope', '$state', 'ContractInfo', 'ContractInfoSearch', 'ParseLinks', 'AlertService', 'paginationConstants', 'pagingParams'];

    function ContractInfoController ($rootScope, $http, $scope, $state, ContractInfo, ContractInfoSearch, ParseLinks, AlertService, paginationConstants, pagingParams) {
        var vm = this;

        vm.loadPage = loadPage;
        vm.predicate = pagingParams.predicate;
        vm.reverse = pagingParams.ascending;
        vm.transition = transition;
        vm.itemsPerPage = paginationConstants.itemsPerPage;
        vm.clear = clear;
        vm.search = search;
        
        vm.searchQuery = {};
        vm.searchQuery.serialNum=pagingParams.serialNum;
        vm.searchQuery.name=pagingParams.name;
        
        vm.types = [{ key: 1, val: '产品' }, { key: 2, val: '外包' },{ key: 3, val: '硬件' },{ key: 4, val: '公共成本' }];
        vm.isPrepareds = [{ key: 'false', val: '正式合同'}, { key: 'true', val: '预立合同'}];
        vm.isEpibolics = [{ key: 'true', val: '外部合同'}, { key: 'false', val: '内部合同'}];
        vm.statuss = [{ key: 1, val: '进行中'}, { key: 2, val: '已完成'}, { key: 3, val: '已删除'}];
        
        for(var j = 0; j < vm.types.length; j++){
        	if(pagingParams.type == vm.types[j].key){
        		vm.searchQuery.type = vm.types[j];
        	}
        }
        for(var j = 0; j < vm.isPrepareds.length; j++){
        	if(pagingParams.isPrepared == vm.isPrepareds[j].key){
        		vm.searchQuery.isPrepared = vm.isPrepareds[j];
        	}
        }
        for(var j = 0; j < vm.isEpibolics.length; j++){
        	if(pagingParams.isEpibolic == vm.isEpibolics[j].key){
        		vm.searchQuery.isEpibolic = vm.isEpibolics[j];
        	}
        }
        
        if(vm.searchQuery.serialNum == undefined && vm.searchQuery.name == undefined && vm.searchQuery.type == undefined
    			&& vm.searchQuery.isPrepared == undefined && vm.searchQuery.isEpibolic == undefined){
        	vm.haveSearch = null;
        }else {
			vm.haveSearch = true;
		}
        
        loadAll();
        function loadAll () {
        	if(pagingParams.serialNum == undefined){
        		pagingParams.serialNum="";
        	}
        	if(pagingParams.name == undefined){
        		pagingParams.name="";
        	}
        	if(pagingParams.type == undefined){
        		pagingParams.type="";
        	}
        	if(pagingParams.isPrepared == undefined){
        		pagingParams.isPrepared="";
        	}
        	if(pagingParams.isEpibolic == undefined){
        		pagingParams.isEpibolic="";
        	}
        	if(pagingParams.salesmanId == undefined){
        		pagingParams.salesmanId="";
        	}
        	if(pagingParams.consultantsId == undefined){
        		pagingParams.consultantsId="";
        	}
        	
	    	ContractInfo.query({
	    		serialNum : pagingParams.serialNum,
	    		name : pagingParams.name,
	    		type : pagingParams.type,
	    		isPrepared : pagingParams.isPrepared,
	    		isEpibolic : pagingParams.isEpibolic,
	    		salesmanId:pagingParams.salesmanId,
	    		consultantsId:pagingParams.consultantsId,
	    		page: pagingParams.page - 1,
	    		size: vm.itemsPerPage,
	    		sort: sort()
	         }, onSuccess, onError);

	    	function sort() {
                var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
                if (vm.predicate !== 'wci.id') {
                    result.push('wci.id');
                }
                return result;
            }
            function onSuccess(data, headers) {
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
                vm.queryCount = vm.totalItems;
                vm.contractInfos = handleData(data);
                vm.page = pagingParams.page;
            }
            function onError(error) {
                AlertService.error(error.data.message);
            }
        }
        function handleData(data){
        	if(data.length>0){
        		for (var i = 0; i< data.length; i++) {
        			for(var j = 0; j < vm.types.length; j++){
        	        	if(data[i].type == vm.types[j].key){
        	        		data[i].typeName = vm.types[j].val;
        	        	}
        	        }
        	        for(var j = 0; j < vm.isPrepareds.length; j++){
        	        	if(data[i].isPrepared == vm.isPrepareds[j].key){
        	        		data[i].isPreparedName = vm.isPrepareds[j].val;
        	        	}
        	        }
        	        for(var j = 0; j < vm.isEpibolics.length; j++){
        	        	if(data[i].isEpibolic == vm.isEpibolics[j].key){
        	        		data[i].isEpibolicName = vm.isEpibolics[j].val;
        	        	}
        	        }
        	        for(var j = 0; j < vm.statuss.length; j++){
        	        	if(data[i].status == vm.statuss[j].key){
        	        		data[i].statusName = vm.statuss[j].val;
        	        	}
        	        }
        	        if(data[i].isPrepared){
        	        	data[i].isPreparedName = "预立合同";
        	        }else{
        	        	data[i].isPreparedName = "正式合同";
        	        }
        	        if(data[i].isEpibolic){
        	        	data[i].isEpibolicName = "外部合同";
        	        }else{
        	        	data[i].isEpibolicName = "内部合同";
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
                serialNum:vm.searchQuery.serialNum,
                name:vm.searchQuery.name,
                type: vm.searchQuery.type != undefined ? vm.searchQuery.type.key : "",
                isPrepared:vm.searchQuery.isPrepared != undefined ? vm.searchQuery.isPrepared.key : "",
                isEpibolic:vm.searchQuery.isEpibolic != undefined ? vm.searchQuery.isEpibolic.key : ""
            });
        }

        function search() {
        	if(vm.searchQuery.serialNum == undefined && vm.searchQuery.name == undefined && vm.searchQuery.type == undefined
        			&& vm.searchQuery.isPrepared == undefined && vm.searchQuery.isEpibolic == undefined){
                return vm.clear();
            }
            vm.links = null;
            vm.page = 1;
            vm.predicate = 'wci.id';
            vm.reverse = false;
            vm.haveSearch = true;
            vm.transition();
        }

        function clear() {
            vm.links = null;
            vm.page = 1;
            vm.predicate = 'wci.id';
            vm.reverse = false;
            vm.searchQuery = {};
            vm.haveSearch = null;
            vm.transition();
        }
        //员工模态框
        var unsubscribe = $rootScope.$on('cpmApp:deptInfoSelected', function(event, result) {
        	vm.searchQuery.salesman = result.objId;
        	console.log(result);
        });
        
    }
})();
