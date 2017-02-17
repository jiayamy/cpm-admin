(function() {
	'use strict';
	
	angular
		.module('cpmApp')
		.controller('ConsultantBonusDetailController', ConsultantBonusDetailController);
	
	ConsultantBonusDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'ConsultantBonus'];
	
	function ConsultantBonusDetailController($scope, $rootScope, $stateParams, previousState, entity, ConsultantBonus){
		var vm = this;

        vm.consultantBonus = entity;
        vm.previousState = previousState.name;
	}
})();