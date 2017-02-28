(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ExternalQuotationController', ExternalQuotationController);

    ExternalQuotationController.$inject = ['$scope', '$state', 'ExternalQuotation',  'ParseLinks', 'AlertService', 'paginationConstants', 'pagingParams'];

    function ExternalQuotationController ($scope, $state, ExternalQuotation, ParseLinks, AlertService, paginationConstants, pagingParams) {
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
        vm.searchQuery.grade = pagingParams.grade;
        if (vm.searchQuery.grade == undefined){
        	vm.haveSearch = null;
        }else{
        	vm.searchQuery.grade = Math.round(vm.searchQuery.grade);
        	vm.haveSearch = true;
        }
        //加载列表
        loadAll();
        function loadAll () {
            ExternalQuotation.query({
            	grade: pagingParams.grade,
                page: pagingParams.page - 1,
                size: vm.itemsPerPage,
                sort: sort()
            }, onSuccess, onError);
            function sort() {
                var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
                if (vm.predicate !== 'weq.id') {
                    result.push('weq.id');
                }
                return result;
            }
            function onSuccess(data, headers) {
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
                vm.queryCount = vm.totalItems;
                vm.externalQuotations = handleData(data);
                vm.page = pagingParams.page;
            }
            function onError(error) {
                AlertService.error(error.data.message);
            }
            function handleData(data){
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
                grade: vm.searchQuery.grade
            });
        }

        function search() {
            if (vm.searchQuery.grade == undefined){
                return vm.clear();
            }
            vm.searchQuery.grade = Math.round(vm.searchQuery.grade);
            
            vm.links = null;
            vm.page = 1;
            vm.predicate = 'weq.grade';
            vm.reverse = true;
            vm.haveSearch = true;
            vm.transition();
        }

        function clear() {
            vm.links = null;
            vm.page = 1;
            vm.predicate = 'weq.grade';
            vm.reverse = true;
            vm.searchQuery = {};
            vm.haveSearch = null;
            vm.transition();
        }
    }
})();
