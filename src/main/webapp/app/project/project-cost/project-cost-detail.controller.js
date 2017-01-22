(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ProjectCostDetailController', ProjectCostDetailController);

    ProjectCostDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity','pageType', 'ProjectCost'];

    function ProjectCostDetailController($scope, $rootScope, $stateParams, previousState, entity,pageType, ProjectCost) {
        var vm = this;

        vm.projectCost = entity;
        
        if(pageType == 1){
        	vm.types = [{key:1,val:'工时'}];
        	vm.canEdit = false;
        	vm.projectCostDetailTitle = "cpmApp.projectCost.detail.timesheetTitle";
        }else{
        	vm.types = [{key:2,val:'差旅'},{key:3,val:'采购'},{key:4,val:'商务'}];
        	vm.canEdit = true;
        	vm.projectCostDetailTitle = "cpmApp.projectCost.detail.title";
        }
        vm.statuss = [{key:1,val:'正常'},{key:2,val:'删除'}];
        for(var j = 0; j < vm.types.length; j++){
        	if(vm.projectCost.type == vm.types[j].key){
        		vm.projectCost.typeName = vm.types[j].val;
        	}
        }
		for(var j = 0; j < vm.statuss.length; j++){
        	if(vm.projectCost.status == vm.statuss[j].key){
        		vm.projectCost.statusName = vm.statuss[j].val;
        	}
        }
        
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('cpmApp:projectCostUpdate', function(event, result) {
            vm.projectCost = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
