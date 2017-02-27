(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('BonusDetailController', BonusDetailController);

    BonusDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'Bonus','ParseLinks','paginationConstants','AlertService'];

    function BonusDetailController($scope, $rootScope, $stateParams, previousState, entity, Bonus,ParseLinks,paginationConstants,AlertService) {
        var vm = this;
        vm.itemsPerPage = paginationConstants.itemsPerPage;
        vm.bonus = entity;
        vm.previousState = previousState.name;
        vm.find = find;
        vm.page= 1;
      //加载列表信息
        find();
        
        function find () {
        	if(entity.contractId == undefined){
        		entity.contractId = "";
        	}
        	Bonus.queryDetail({
        		contractId: entity.contractId,
        		page: vm.page-1,
                size: vm.itemsPerPage,
            }, onSuccess, onError);
        }
        
    	function onSuccess(data, headers) {
            vm.links = ParseLinks.parse(headers('link'));
            vm.totalItems = headers('X-Total-Count');
            vm.queryCount = vm.totalItems;
            vm.bonuses = handleData(data);
        }
    	
    	function onError(error) {
            AlertService.error(error.data.message);
        }
        
        function handleData(data){
        	return data;
        }
    }
})();
