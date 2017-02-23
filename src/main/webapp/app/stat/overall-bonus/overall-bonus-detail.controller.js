(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('OverallBonusDetailController', OverallBonusDetailController);

    OverallBonusDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'OverallBonus','ParseLinks','paginationConstants','AlertService'];

    function OverallBonusDetailController($scope, $rootScope, $stateParams, previousState, entity, OverallBonus,ParseLinks,paginationConstants,AlertService) {
        var vm = this;
        vm.itemsPerPage = paginationConstants.itemsPerPage;
        vm.overallBonus = entity;
        vm.previousState = previousState.name;
        vm.find = find;
        vm.page= 1;
      //加载列表信息
        find();
        
        function find () {
        	if(entity.contractId == undefined){
        		entity.contractId = "";
        	}
        	OverallBonus.queryDetail({
        		contractId: entity.contractId,
        		page: vm.page-1,
                size: vm.itemsPerPage,
            }, onSuccess, onError);
        }
        
    	function onSuccess(data, headers) {
            vm.links = ParseLinks.parse(headers('link'));
            vm.totalItems = headers('X-Total-Count');
            vm.queryCount = vm.totalItems;
            vm.overallBonuses = handleData(data);
        }
    	
    	function onError(error) {
            AlertService.error(error.data.message);
        }
        
        function handleData(data){
        	return data;
        }
    }
})();
