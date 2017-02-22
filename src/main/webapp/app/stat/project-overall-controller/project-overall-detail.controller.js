(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ProjectOverallDetailController', ProjectOverallDetailController);

    ProjectOverallDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'ProjectOverall','ParseLinks','paginationConstants','AlertService'];

    function ProjectOverallDetailController($scope, $rootScope, $stateParams, previousState, entity, ProjectOverall,ParseLinks,paginationConstants,AlertService) {
        var vm = this;
        vm.itemsPerPage = paginationConstants.itemsPerPage;
        vm.projectOverall = entity;
        vm.previousState = previousState.name;
        vm.find = find;
        vm.page= 1;
      //加载列表信息
        find();
        
        function find () {
        	if(entity.contractId == undefined){
        		entity.contractId = "";
        	}
        	ProjectOverall.queryDetail({
        		contractId: entity.contractId,
        		page: vm.page-1,
                size: vm.itemsPerPage,
            }, onSuccess, onError);
        }
        
    	function onSuccess(data, headers) {
            vm.links = ParseLinks.parse(headers('link'));
            vm.totalItems = headers('X-Total-Count');
            vm.queryCount = vm.totalItems;
            vm.projectOverAlls = handleData(data);
        }
    	
    	function onError(error) {
            AlertService.error(error.data.message);
        }
        
        function handleData(data){
        	if (data.length > 0) {
				for(var i = 0; i< data.length ; i++){
					if (data[i].salesman == "") {
						data[i].salesman = data[i].consultants;
					}
				}
			}
        	return data;
        }
    }
})();
