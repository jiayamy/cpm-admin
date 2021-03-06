(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('UserCostDetailController', UserCostDetailController);

    UserCostDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'UserCost'];

    function UserCostDetailController($scope, $rootScope, $stateParams, previousState, entity, UserCost) {
    	var vm = this;
        
        vm.userCost = entity;
        vm.previousState = previousState.name;
        
        UserCost.getSerialNumByuserId({id:entity.userId},function(data){
        	vm.serialNum = data.serialNum;
        },function(){vm.serialNum = "";});
    }
})();