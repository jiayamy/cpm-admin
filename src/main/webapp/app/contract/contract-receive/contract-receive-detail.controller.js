(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ContractReceiveDetailController', ContractReceiveDetailController);

    ContractReceiveDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'ContractReceive'];

    function ContractReceiveDetailController($scope, $rootScope, $stateParams, previousState, entity, ContractReceive) {
        var vm = this;

        vm.contractReceive = entity;
        vm.previousState = previousState.name;

        vm.statuss = [{key:1,val:'可用'},{key:2,val:'删除'}];
        for(var j = 0; j < vm.statuss.length; j++){
			if(vm.contractReceive.status == vm.statuss[j].key){
				vm.contractReceive.statusName = vm.statuss[j].val;
			}
		}
    }
})();
