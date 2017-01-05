(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ProjectInfoController', ProjectInfoController);

    ProjectInfoController.$inject = ['$scope', '$state', 'ProjectInfo', 'ProjectInfoSearch', 'ParseLinks', 'AlertService', 'paginationConstants', 'pagingParams'];

    function ProjectInfoController ($scope, $state, ProjectInfo, ProjectInfoSearch, ParseLinks, AlertService, paginationConstants, pagingParams) {
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

        loadAll();

        function loadAll () {
            ProjectInfo.query({
            	contractId:vm.searchQuery.contractId,
            	serialNum:vm.searchQuery.serialNum,
            	name:vm.searchQuery.name,
            	status:vm.searchQuery.status,
                page: pagingParams.page - 1,
                size: vm.itemsPerPage,
                sort: sort()
            }, onSuccess, onError);
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
                vm.projectInfos = data;
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
                contractId:vm.searchQuery.contractId,
            	serialNum:vm.searchQuery.serialNum,
            	name:vm.searchQuery.name,
            	status:vm.searchQuery.status
            });
        }

        function search() {
            if (!vm.searchQuery.contractId && !vm.searchQuery.serialNum
            		&& !vm.searchQuery.name && !vm.searchQuery.status){
                return vm.clear();
            }
            vm.links = null;
            vm.page = 1;
            vm.predicate = 'id';
            vm.reverse = false;
            vm.transition();
        }

        function clear() {
            vm.links = null;
            vm.page = 1;
            vm.predicate = 'id';
            vm.reverse = false;
            vm.searchQuery = {};
            vm.transition();
        }
    }
})();
