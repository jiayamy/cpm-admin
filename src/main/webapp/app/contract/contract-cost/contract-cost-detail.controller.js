(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ContractCostDetailController', ContractCostDetailController);

    ContractCostDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'ContractCost','pageType'];

    function ContractCostDetailController($scope, $rootScope, $stateParams, previousState, entity, ContractCost,pageType) {
        var vm = this;

        vm.isShow = false;
        vm.contractCost = entity;
        if(pageType == 1){
        	vm.types = [{key:1,val:'工时'}];
        	vm.canEdit = false;
        	vm.contractCostDetailTitle = "cpmApp.contractCost.detail.timesheetTitle";
        	vm.isShow = true;
        }else{
        	vm.types = [{key:2,val:'差旅'},{key:3,val:'采购'},{key:4,val:'商务'}];
        	vm.canEdit = true;
        	vm.contractCostDetailTitle = "cpmApp.contractCost.detail.title";
        }
        vm.statuss = [{key:1,val:'正常'},{key:2,val:'删除'}];
        for(var j = 0; j < vm.types.length; j++){
        	if(vm.contractCost.type == vm.types[j].key){
        		vm.contractCost.typeName = vm.types[j].val;
        	}
        }
		for(var j = 0; j < vm.statuss.length; j++){
        	if(vm.contractCost.status == vm.statuss[j].key){
        		vm.contractCost.statusName = vm.statuss[j].val;
        	}
        }
        vm.previousState = previousState.name;

        var unsubscribe = $rootScope.$on('cpmApp:contractCostUpdate', function(event, result) {
            vm.contractCost = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
