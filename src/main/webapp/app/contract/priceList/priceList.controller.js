(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('PriceListController', PriceListController);

    PriceListController.$inject = ['$scope','PriceListService','ParseLinks','$state','pagingParams','paginationConstants','AlertService'];

    function PriceListController ($scope,PriceListService,ParseLinks,$state,pagingParams,paginationConstants,AlertService) {
        var vm = this;
        vm.priceList = null;
        vm.links = null;
        vm.loadAll = loadAll;
        vm.setActive = setActive;
        vm.prices = [];
        vm.loadPage = loadPage;
        vm.onChangeData = onChangeData;
        vm.page = 1;
        vm.totalItems = null;
        vm.name = null;
        vm.type = null;
        vm.source = null;
        vm.transition = transition;
        vm.predicate = pagingParams.predicate;
        vm.reverse = pagingParams.ascending;
        vm.itemsPerPage = paginationConstants.itemsPerPage;
        vm.onChangeData();
        
        vm.loadAll();
        function setActive (user, isActivated) {
            user.activated = isActivated;
            PriceListService.update(user, function () {
                vm.loadAll();
                vm.clear();
            });
        }
        
        function setActive (user, isActivated) {
            user.activated = isActivated;
            PriceListService.update(user, function () {
                vm.loadAll();
                vm.clear();
            });
        }
        
        function loadAll () {
            PriceListService.query({
                page: pagingParams.page - 1,
                size: vm.itemsPerPage,
                sort: sort()
            }, onSuccess, onError);
        }
        
        function onSuccess(data, headers) {
            //hide anonymous user from user management: it's a required user for Spring Security
            var hiddenUsersSize = 0;
            for (var i in data) {
                if (data[i]['login'] === 'anonymoususer') {
                    data.splice(i, 1);
                    hiddenUsersSize++;
                }
            }
            vm.links = ParseLinks.parse(headers('link'));
            vm.totalItems = headers('X-Total-Count') - hiddenUsersSize;
            vm.queryCount = vm.totalItems;
            vm.page = pagingParams.page;
            vm.prices = data;
        }
        
        function onError(error) {
            AlertService.error(error.data.message);
        }
        
        function clear () {
            vm.price = {
                id: null, name: null, type: null, source: null, creator: null,
                creatTime: null, updator: null, updateTime: null, createdDate: null,
                unit: null, unitPrice: null
            };
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
            PriceListService.query({page: vm.page -1, size: 2, name: name, type: type,source: source}, function(result, headers){
                vm.priceList = result;
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
            });
      }
        function sort () {
            var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
            if (vm.predicate !== 'id') {
                result.push('id');
            }
            return result;
        }
        
        function loadPage (page) {
            vm.page = page;
            vm.transition();
            vm.onChangeData();
       }
        function transition () {
            $state.transitionTo($state.$current, {
                page: vm.page,
                sort: vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc'),
                search: vm.currentSearch
            });
        }
    }
})();
