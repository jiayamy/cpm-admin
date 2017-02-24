(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('BonusRateController', BonusRateController);

    BonusRateController.$inject = ['$scope', '$state', 'BonusRate','DeptType',  'ParseLinks', 'AlertService', 'paginationConstants', 'pagingParams'];

    function BonusRateController ($scope, $state, BonusRate,DeptType, ParseLinks, AlertService, paginationConstants, pagingParams) {
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
        vm.searchQuery.deptType = pagingParams.deptType;
        vm.searchQuery.contractType = pagingParams.contractType;

        if (!vm.searchQuery.deptType && !vm.searchQuery.contractType){
        	vm.haveSearch = null;
        }else{
        	vm.haveSearch = true;
        }
        //合同类型
        vm.contractTypes = [{ key: 1, val: '产品' }, { key: 2, val: '外包' },{ key: 3, val: '硬件' },{ key: 4, val: '公共成本' }
        ,{ key: 5, val: '项目' },{ key: 6, val: '推广' },{ key: 7, val: '其他' }];
        for(var j = 0; j < vm.contractTypes.length; j++){
        	if(pagingParams.contractType == vm.contractTypes[j].key){
        		vm.searchQuery.contractType = vm.contractTypes[j];
        	}
        }
        //部门类型
        loadDeptType();
        function loadDeptType(){
        	DeptType.getAllForCombox(
    			{
        		},
        		function(data, headers){
        			vm.deptTypes = data;
            		if(vm.deptTypes && vm.deptTypes.length > 0){
            			for(var i = 0; i < vm.deptTypes.length; i++){
            				if(pagingParams.deptType == vm.deptTypes[i].key){
            					vm.searchQuery.deptType = vm.deptTypes[i];
            				}
            			}
            		}
        		},
        		function(error){
        			AlertService.error(error.data.message);
        		}
        	);
        }
        //加载列表
        loadAll();
        function loadAll () {
            BonusRate.query({
            	deptType: pagingParams.deptType,
            	contractType: pagingParams.contractType,
                page: pagingParams.page - 1,
                size: vm.itemsPerPage,
                sort: sort()
            }, onSuccess, onError);
            function sort() {
                var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
                if (vm.predicate !== 'wbr.id') {
                    result.push('wbr.id');
                }
                return result;
            }
            function onSuccess(data, headers) {
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
                vm.queryCount = vm.totalItems;
                vm.bonusRates = handleData(data);
                vm.page = pagingParams.page;
            }
            function onError(error) {
                AlertService.error(error.data.message);
            }
            function handleData(data){
            	if(data && data.length > 0){
            		for(var i = 0; i< data.length ; i++){
            			for(var j = 0; j < vm.contractTypes.length; j++){
            	        	if(data[i].contractType == vm.contractTypes[j].key){
            	        		data[i].contractTypeName = vm.contractTypes[j].val;
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
                deptType: vm.searchQuery.deptType ? vm.searchQuery.deptType.key : "",
                contractType: vm.searchQuery.contractType ? vm.searchQuery.contractType.key : ""
            });
        }

        function search() {
            if (!vm.searchQuery.deptType && !vm.searchQuery.contractType){
                return vm.clear();
            }
            vm.links = null;
            vm.page = 1;
            vm.predicate = 'wbr.contractType';
            vm.reverse = false;
            vm.haveSearch = true;
            vm.transition();
        }

        function clear() {
            vm.links = null;
            vm.page = 1;
            vm.predicate = 'wbr.contractType';
            vm.reverse = false;
            vm.searchQuery = {};
            vm.haveSearch = null;
            vm.transition();
        }
    }
})();
