(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ProjectSupportBonusDetailController', ProjectSupportBonusDetailController);

    ProjectSupportBonusDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'ProjectSupportBonus','ParseLinks','paginationConstants','AlertService'];

    function ProjectSupportBonusDetailController($scope, $rootScope, $stateParams, previousState, entity, ProjectSupportBonus,ParseLinks,paginationConstants,AlertService) {
        var vm = this;
        vm.itemsPerPage = paginationConstants.itemsPerPage;
        vm.projectSupportBonus = entity;
        vm.previousState = previousState.name;
        vm.find = find;
        vm.page= 1;
      //加载列表信息
        find();
        
        function find () {
        	if(entity.contractId == undefined){
        		entity.contractId = "";
        	}
        	ProjectSupportBonus.queryDetail({
        		contractId: entity.contractId,
        		page: vm.page-1,
                size: vm.itemsPerPage,
            }, onSuccess, onError);
        }
        
    	function onSuccess(data, headers) {
            vm.links = ParseLinks.parse(headers('link'));
            vm.totalItems = headers('X-Total-Count');
            vm.queryCount = vm.totalItems;
            vm.projectSupportBonuses = handleData(data);
        }
    	
    	function onError(error) {
            AlertService.error(error.data.message);
        }
        
        function handleData(data){
        	return data;
        }
    }
})();
