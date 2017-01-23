(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ContractCostDetailController', ContractCostDetailController);

    ContractCostDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'ContractCost'];

    function ContractCostDetailController($scope, $rootScope, $stateParams, previousState, entity, ContractCost) {
        var vm = this;

        vm.contractCost = entity;
        vm.types = [{key:1,val:'工时'},{key:2,val:'差旅'},{key:3,val:'采购'},{key:4,val:'商务'}];
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
