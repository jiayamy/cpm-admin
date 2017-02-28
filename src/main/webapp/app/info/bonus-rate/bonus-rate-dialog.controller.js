(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('BonusRateDialogController', BonusRateDialogController);

    BonusRateDialogController.$inject = ['$timeout', '$state', '$rootScope','$scope', '$stateParams','previousState', 'entity', 'BonusRate','AlertService','DeptType'];

    function BonusRateDialogController ($timeout, $state, $rootScope,$scope, $stateParams,previousState, entity, BonusRate,AlertService,DeptType) {
        var vm = this;
      
        vm.previousState = previousState.name;
        
        vm.bonusRate = entity;
        
        vm.contractTypeDisable = vm.bonusRate.id;
        if (entity.id != null){
        	vm.bonusRateDisable = true;
        }
        
        vm.save = save;
        
      //合同类型
        vm.contractTypes = [{ key: 1, val: '产品' }, { key: 2, val: '外包' },{ key: 3, val: '硬件' },{ key: 4, val: '公共成本' }
        ,{ key: 5, val: '项目' },{ key: 6, val: '推广' },{ key: 7, val: '其他' }];
        
        for(var j = 0; j < vm.contractTypes.length; j++){
        	if(vm.bonusRate.contractType == vm.contractTypes[j].key){
        		vm.bonusRate.contractType = vm.contractTypes[j];
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
            				if(vm.bonusRate.deptType == vm.deptTypes[i].key){
            					vm.bonusRate.deptType = vm.deptTypes[i];
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
            
           var bonusRate = {};
           bonusRate.id = vm.bonusRate.id;
           bonusRate.rate = vm.bonusRate.rate;
           bonusRate.contractType = vm.bonusRate.contractType ? vm.bonusRate.contractType.key : "";
           bonusRate.deptType = vm.bonusRate.deptType ? vm.bonusRate.deptType.key : "";
           
           BonusRate.update(bonusRate, onSaveSuccess, onSaveError);
        }

        function onSaveSuccess (result) {
            $scope.$emit('cpmApp:bonusRateUpdate', result);
            $state.go('bonus-rate');
            vm.isSaving = false;
        }

        function onSaveError (result) {
            vm.isSaving = false;
        }
    }
})();
