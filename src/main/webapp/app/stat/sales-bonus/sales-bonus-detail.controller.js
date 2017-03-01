(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('SalesBonusDetailController', SalesBonusDetailController);

    SalesBonusDetailController.$inject = ['$scope', '$state', 'SalesBonus', 'ParseLinks', 'AlertService', 'paginationConstants', 'pagingParams', 'entity', 'previousState'];

    function SalesBonusDetailController ($scope, $state, SalesBonus, ParseLinks, AlertService, paginationConstants, pagingParams, entity, previousState) {
        var vm = this;
        vm.predicate = pagingParams.predicate;
        vm.reverse = pagingParams.ascending;
        vm.itemsPerPage = paginationConstants.itemsPerPage;
        vm.previousState = previousState.name;
        vm.page= 1;
        vm.loadAll = loadAll;
        //加载列表数据
        loadAll();
        function loadAll () {
        	SalesBonus.queryDetail({
        		id: entity.id,
                page: vm.page - 1,
                size: vm.itemsPerPage,
                sort: sort()
            }, onSuccess, onError);
            function onSuccess(data, headers) {
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
                vm.queryCount = vm.totalItems;
                vm.salesBonuss = handleData(data);
            }
            function sort() {
                var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
                if (vm.predicate !== 'wsb.id') {
                    result.push('wsb.id');
                }
                return result;
            }
            function onError(error) {
                AlertService.error(error.data.message);
            }
            function handleData(data){
            	return data;
            }
        }
    }
})();
