(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ShareCostRateDialogController', ShareCostRateDialogController);

    ShareCostRateDialogController.$inject = ['$timeout', '$state', '$rootScope','$scope', '$stateParams','previousState', 'entity', 'ShareCostRate','AlertService','DeptType'];

    function ShareCostRateDialogController ($timeout, $state, $rootScope,$scope, $stateParams,previousState, entity, ShareCostRate,AlertService,DeptType) {
        var vm = this;
      
        vm.previousState = previousState.name;
        
        vm.shareCostRate = entity;
        
        vm.contractTypeDisable = vm.shareCostRate.id;
        if (entity.id != null){
        	vm.shareCostRateDisable = true;
        }
        
        vm.save = save;
        
      //合同类型
        vm.contractTypes = [{ key: 1, val: '产品' }, { key: 2, val: '外包' },{ key: 3, val: '硬件' },{ key: 4, val: '公共成本' }
        ,{ key: 5, val: '项目' },{ key: 6, val: '推广' },{ key: 7, val: '其他' }];
        
        for(var j = 0; j < vm.contractTypes.length; j++){
        	if(vm.shareCostRate.contractType == vm.contractTypes[j].key){
        		vm.shareCostRate.contractType = vm.contractTypes[j];
        	}
        }
      //部门类型
        loadDeptType();
        function loadDeptType(){
        	DeptType.getAllForCombox(
    			{
        		},
        		function(data, headers){
        			vm.deptTypes = data;
            		if(vm.deptTypes && vm.deptTypes.length > 0){
            			for(var i = 0; i < vm.deptTypes.length; i++){
            				if(vm.shareCostRate.deptType == vm.deptTypes[i].key){
            					vm.shareCostRate.deptType = vm.deptTypes[i];
            				}
            			}
            		}
        		},
        		function(error){
        			AlertService.error(error.data.message);
        		}
        	);
        }
        function save () {
            vm.isSaving = true;
            
           var shareCostRate = {};
           shareCostRate.id = vm.shareCostRate.id;
           shareCostRate.shareRate = vm.shareCostRate.shareRate;
           shareCostRate.contractType = vm.shareCostRate.contractType ? vm.shareCostRate.contractType.key : "";
           shareCostRate.deptType = vm.shareCostRate.deptType ? vm.shareCostRate.deptType.key : "";
           
           ShareCostRate.update(shareCostRate, onSaveSuccess, onSaveError);
        }

        function onSaveSuccess (result,headers) {
            if(headers("X-cpmApp-alert") == 'cpmApp.shareCostRate.updated'){
    			$state.go(vm.previousState);
    		}
            vm.isSaving = false;
        }

        function onSaveError (result) {
            vm.isSaving = false;
        }
    }
})();
