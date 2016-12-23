(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('PriceListController', PriceListController);

    PriceListController.$inject = ['$scope','PriceListService','ParseLinks'];

    function PriceListController ($scope,PriceListService,ParseLinks) {
        var vm = this;
        vm.priceList = null;
        vm.links = null;
        vm.loadPage = loadPage;
        vm.onChangeData = onChangeData;
        vm.page = 1;
        vm.totalItems = null;
        vm.name = null;
        var type = null;
        var source = null;
        vm.onChangeData();

        function onChangeData () {
//            var name = $filter('text')(vm.toName);
//            var type = $filter('text')(vm.toType);
//            var psource = $filter('text')(vm.toPSource);
        	var name = $scope.name;
        	if(name == undefined){
        		name = "";
        	}
 //       	var type = $scope.type;
//        	var source = $scope.source;
            
            PriceListService.query({page: vm.page -1, size: 2, name: name, type: type,source: source}, function(result, headers){
                vm.priceList = result;
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
            });
      }


        function loadPage (page) {
            vm.page = page;
            vm.onChangeData();
       }
    }
})();
