(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ContractInfoDialogController', ContractInfoDialogController);

    ContractInfoDialogController.$inject = ['previousState','$rootScope','$timeout', '$scope', '$stateParams','entity', 'ContractInfo','$state'];

    function ContractInfoDialogController (previousState,$rootScope,$timeout, $scope, $stateParams, entity, ContractInfo, $state) {
        var vm = this;

        vm.contractInfo = entity;

        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        
        vm.previousState = previousState.name;
        vm.queryDept = previousState.queryDept;
        vm.types = [{ key: 1, val: '产品' }, { key: 2, val: '外包' },{ key: 3, val: '硬件' },{ key: 4, val: '公共成本' }];

        for(var j = 0; j < vm.types.length; j++){
        	if(vm.contractInfo.type == vm.types[j].key){
        		vm.contractInfo.type = vm.types[j];
        	}
        }
        
        function save () {
        	vm.isSaving = true;
        	console.log(vm.contractInfo);
        	var contractInfo = {};
        	contractInfo.id = vm.contractInfo.id;
        	contractInfo.serialNum = vm.contractInfo.serialNum;
        	contractInfo.name = vm.contractInfo.name;
        	contractInfo.amount = vm.contractInfo.amount;
        	
        	contractInfo.type = vm.contractInfo.type ? vm.contractInfo.type.key : "";
        	contractInfo.isPrepared = vm.contractInfo.isPrepared;
        	contractInfo.isEpibolic = vm.contractInfo.isEpibolic;
        	
        	contractInfo.salesmanId = vm.contractInfo.salesmanId;
        	contractInfo.salesman = vm.contractInfo.salesman;
        	contractInfo.deptId = vm.contractInfo.deptId;
        	contractInfo.dept = vm.contractInfo.dept;
        	
        	contractInfo.consultantsId = vm.contractInfo.consultantsId;
        	contractInfo.consultants = vm.contractInfo.consultants;
        	contractInfo.consultantsDeptId = vm.contractInfo.consultantsDeptId;
        	contractInfo.consultantsDept = vm.contractInfo.consultantsDept;
        	
        	contractInfo.startDay = vm.contractInfo.startDay;
        	contractInfo.endDay = vm.contractInfo.endDay;
        	contractInfo.taxRate = vm.contractInfo.taxRate;
        	contractInfo.taxes = vm.contractInfo.taxes;
        	contractInfo.shareRate = vm.contractInfo.shareRate;
        	contractInfo.shareCost = vm.contractInfo.shareCost;
        	contractInfo.paymentWay = vm.contractInfo.paymentWay;
        	
        	contractInfo.contractor = vm.contractInfo.contractor;
        	contractInfo.address = vm.contractInfo.address;
        	contractInfo.postcode = vm.contractInfo.postcode;
        	contractInfo.linkman = vm.contractInfo.linkman;
        	contractInfo.contactDept = vm.contractInfo.contactDept;
        	contractInfo.telephone = vm.contractInfo.telephone;

        	ContractInfo.update(contractInfo,
	    		function(data, headers){
            		vm.isSaving = false;
            		$state.go(vm.previousState);
	        	},
	        	function(error){
	        		vm.isSaving = false;
	        	}
        	);       
        }

        vm.datePickerOpenStatus.startDay = false;
        vm.datePickerOpenStatus.endDay = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
        //部门的处理
        var unsubscribe = $rootScope.$on('cpmApp:deptInfoSelected', function(event, result, dataType) {
        	console.log(dataType);
        	if(dataType == 1){
        		vm.contractInfo.salesmanId = result.objId;
        		vm.contractInfo.salesman = result.name;
        		vm.contractInfo.deptId = result.parentId;
        		vm.contractInfo.dept = result.parentName;
        	}else{
        		vm.contractInfo.consultantsId = result.objId;
        		vm.contractInfo.consultants = result.name;
        		vm.contractInfo.consultantsDeptId = result.parentId;
        		vm.contractInfo.consultantsDept = result.parentName;
        	}
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
