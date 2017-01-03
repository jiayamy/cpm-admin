(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ProductPriceController', ProductPriceController);

    ProductPriceController.$inject = ['$scope', '$state', 'ProductPrice', 'ProductPriceSearch', 'ParseLinks', 'AlertService', 'paginationConstants', 'pagingParams'];

    function ProductPriceController ($scope, $state, ProductPrice, ProductPriceSearch, ParseLinks, AlertService, paginationConstants, pagingParams) {
        var vm = this;
        vm.price = null;
        vm.links = null;
        vm.loadPage = loadPage;
        vm.predicate = pagingParams.predicate;
        vm.reverse = pagingParams.ascending;
        vm.transition = transition;
        vm.itemsPerPage = paginationConstants.itemsPerPage;
        vm.clear = clear;
        vm.search = search;
        vm.loadAll = loadAll;
        vm.searchQuery = pagingParams.search;
        vm.currentSearch = pagingParams.search;
        vm.onChangeData = onChangeData;
        vm.onChangeData();
        loadAll();

        function loadAll () {
            if (pagingParams.search) {
                ProductPriceSearch.query({
                    query: pagingParams.search,
                    page: pagingParams.page - 1,
                    size: vm.itemsPerPage,
                    sort: sort()
                }, onSuccess, onError);
            } else {
                ProductPrice.query({
                    page: pagingParams.page - 1,
                    size: vm.itemsPerPage,
                    sort: sort()
                }, onSuccess, onError);
            }
            function sort() {
                var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
                if (vm.predicate !== 'id') {
                    result.push('id');
                }
                return result;
            }
            function onSuccess(data, headers) {
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
                vm.queryCount = vm.totalItems;
                vm.productPrices = data;
                vm.page = pagingParams.page;
            }
            function onError(error) {
                AlertService.error(error.data.message);
            }
        }
        function onChangeData () {
        	var name = $scope.name;
        	if(name == undefined){
        		name = "";
        	}
        	var type = $scope.type;
        	if(type == undefined){
        		type = "";
        	}
        	var source = $scope.source;
            if (source == undefined) {
				source = "";
			}
            ProductPriceSearch.query({page: vm.page -1, size: 2, name: name, type: type,source: source}, function(result, headers){
                vm.price = result;
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
            });
      }
        function loadPage(page) {
            vm.page = page;
            vm.transition();
            vm.onChangeData();
        }

        function transition() {
            $state.transitionTo($state.$current, {
                page: vm.page,
                sort: vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc'),
                search: vm.currentSearch
            });
        }

        function search(searchQuery) {
            if (!searchQuery){
                return vm.clear();
            }
            vm.links = null;
            vm.page = 1;
            vm.predicate = '_score';
            vm.reverse = false;
            vm.currentSearch = searchQuery;
            vm.transition();
        }

        function clear() {
            vm.links = null;
            vm.page = 1;
            vm.predicate = 'id';
            vm.reverse = true;
            vm.currentSearch = null;
            vm.transition();
        }
    }
})();
