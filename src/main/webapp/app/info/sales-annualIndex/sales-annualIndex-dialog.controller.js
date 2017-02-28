(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('SalesAnnualIndexDialogController', SalesAnnualIndexDialogController);

    SalesAnnualIndexDialogController.$inject = ['$scope', '$rootScope', '$state', '$stateParams', 'previousState', 'entity', 'SalesAnnualIndex','AlertService','DateUtils'];

    function SalesAnnualIndexDialogController ($scope, $rootScope, $state, $stateParams, previousState, entity, SalesAnnualIndex, AlertService, DateUtils) {
        var vm = this;

        vm.salesAnnualIndex = entity;
        vm.salesAnnualIndex.statYear= DateUtils.convertYYYYToDate(vm.salesAnnualIndex.statYear);
        
        vm.infoDisable = vm.salesAnnualIndex.id;
        
        vm.previousState = previousState.name;
        vm.queryDept = previousState.queryDept;
        
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        
        function save () {
            vm.isSaving = true;
            var salesAnnualIndex = {};
            salesAnnualIndex.id = vm.salesAnnualIndex.id;
            salesAnnualIndex.userId = vm.salesAnnualIndex.userId;
            salesAnnualIndex.userName = vm.salesAnnualIndex.userName;
            salesAnnualIndex.statYear = DateUtils.convertLocalDateToFormat(vm.salesAnnualIndex.statYear,"yyyy");
            salesAnnualIndex.annualIndex = vm.salesAnnualIndex.annualIndex;
            
            SalesAnnualIndex.update(salesAnnualIndex,
	    		function(data, headers){
            		vm.isSaving = false;
//            		AlertService.error(error.data.message);
            		if(headers("X-cpmApp-alert") == 'cpmApp.salesAnnualIndex.updated'){
            			$state.go(vm.previousState);
            		}
	        	},
	        	function(error){
	        		vm.isSaving = false;
//	        		AlertService.error(error.data.message);
	        	}
	        );            
        }
        vm.datePickerOpenStatus.statYear = false;
        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
        
        var unsubscribe = $rootScope.$on('cpmApp:deptInfoSelected', function(event, result) {
        	vm.salesAnnualIndex.userId = result.objId;
        	vm.salesAnnualIndex.userName = result.name;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
