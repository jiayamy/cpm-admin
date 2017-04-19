(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ProductPriceController', ProductPriceController);

    ProductPriceController.$inject = ['$scope', '$state', 'ProductPrice', 'ParseLinks', 'AlertService', 'paginationConstants', 'pagingParams'];

    function ProductPriceController ($scope, $state, ProductPrice, ParseLinks, AlertService, paginationConstants, pagingParams) {
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
        var source = pagingParams.source;
        if(source){
        	if(source == 2){
        		source = { id: 2, name: '外部' };
        	}else if(source == 1){
        		source = { id: 1, name: '内部' };
        	}
        }
        var type = pagingParams.type;
        if(type){
        	if(type == 1){
        		type = { id: 1, name: '硬件' };
        	}else if(type == 2){
        		type = { id: 2, name: '软件' };
        	}
        }
        vm.searchQuery.source= source;
        vm.searchQuery.type = type;
        vm.searchQuery.name = pagingParams.name;
        vm.currentSearch = {};
        vm.currentSearch.source = source;
        vm.currentSearch.type = type;
        vm.currentSearch.name = pagingParams.name;
        if (!vm.currentSearch.source && !vm.currentSearch.type && !vm.currentSearch.name){
        	vm.currentSearch.haveSearch = null;
        }else{
        	vm.currentSearch.haveSearch = true;
        }
        vm.types = [{ id: 1, name: '硬件' }, { id: 2, name: '软件' }];
        vm.sources = [{ id: 2, name: '外部' }, { id: 1, name: '内部' }];
        
        loadAll();

        function loadAll () {
        	if(!pagingParams.type){
        		pagingParams.type = "";
        	}
        	if(!pagingParams.source){
        		pagingParams.source = "";
        	}
        	if(!pagingParams.name){
        		pagingParams.name = "";
        	}
        	ProductPrice.query({
        		source: pagingParams.source,
            	type: pagingParams.type,
            	name: pagingParams.name,
                page: pagingParams.page - 1,
                size: vm.itemsPerPage,
                sort: sort()
            }, onSuccess, onError);
            function sort() {
                var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
                if (vm.predicate !== 'wpp.id') {
                    result.push('wpp.id');
                }
                return result;
            }
            function onSuccess(data, headers) {
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
                vm.queryCount = vm.totalItems;
                vm.productPrices = handleData(data);
                vm.page = pagingParams.page;
            }
            function onError(error) {
                AlertService.error(error.data.message);
            }
        }
        function handleData(data){
        	if(data.length > 0){
        		for(var i = 0; i< data.length ; i++){
        			if(data[i].type == 1){
        				data[i].typeName = "硬件";
        			}else if(data[i].type == 2){
        				data[i].typeName = "软件";
        			}
        			if(data[i].source == 2){
        				data[i].sourceName = "外部";
        			}else if(data[i].source == 1){
        				data[i].sourceName = "内部";
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
                type:vm.currentSearch.type != null ? vm.currentSearch.type.id : "",
                source:vm.currentSearch.source != null ? vm.currentSearch.source.id : "",
                name:vm.currentSearch.name
            });
        }

        function search(searchQuery) {
            if (!searchQuery.type && !searchQuery.source && !searchQuery.name){
                return vm.clear();
            }
            vm.links = null;
            vm.page = 1;
            vm.predicate = '_score';
            vm.reverse = false;
            vm.currentSearch = searchQuery;
            vm.currentSearch.haveSearch = true;
            vm.transition();
        }

        function clear() {
            vm.links = null;
            vm.page = 1;
            vm.predicate = 'wpp.id';
            vm.reverse = false;
            vm.currentSearch = {};
            vm.currentSearch.haveSearch = null;
            vm.transition();
        }
    }
})();
