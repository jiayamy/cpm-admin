(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ContractInfoDialogController', ContractInfoDialogController);

    ContractInfoDialogController.$inject = ['previousState','$rootScope','$timeout', '$scope', '$stateParams','entity', 'ContractInfo','$state'];

    function ContractInfoDialogController (previousState,$rootScope,$timeout, $scope, $stateParams, entity, ContractInfo, $state) {
        var vm = this;

        vm.contractInfo = entity;
        //预立合同可以修改合同编号
        vm.serialNumDisabled = true;
        if(vm.contractInfo.id == undefined || vm.contractInfo.isPrepared == true){
        	vm.serialNumDisabled = false;
        }
        
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        
        vm.previousState = previousState.name;
        vm.queryDept = previousState.queryDept;
        vm.types = [{ key: 1, val: '产品' }, { key: 2, val: '外包' },{ key: 3, val: '硬件' },{ key: 4, val: '公共成本' }
        ,{ key: 5, val: '项目' },{ key: 6, val: '推广' },{ key: 7, val: '其他' }];

        for(var j = 0; j < vm.types.length; j++){
        	if(vm.contractInfo.type == vm.types[j].key){
        		vm.contractInfo.type = vm.types[j];
        	}
        }
        
        function save () {
        	vm.isSaving = true;
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
        	contractInfo.consultantsShareRate = vm.contractInfo.consultantsShareRate;
        	
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
            		if(headers("X-cpmApp-alert") == 'cpmApp.contractInfo.updated'){
            			$state.go(vm.previousState);
            		}
	        	},
	        	function(error){
	        		vm.isSaving = false;
	        	}
        	);       
        }
        vm.serialNumChanged = serialNumChanged;
        function serialNumChanged(){
        	if(vm.contractInfo.serialNum == undefined){
        		vm.contractInfo.isPrepared = false;
        	}else{
        		var s = vm.contractInfo.serialNum.toUpperCase();
        		if(s.substr(0,2) == "WY"){
        			vm.contractInfo.isPrepared = true;
        		}else{
        			vm.contractInfo.isPrepared = false;
        		}
        	}
        }
        
        vm.amountChanged = amountChanged;
        vm.taxRateChanged = taxRateChanged;
        vm.taxesChanged = taxesChanged;
        vm.shareRateChanged = shareRateChanged;
        vm.shareCostChanged = shareCostChanged;
        
        function amountChanged(){
        	if(vm.contractInfo.amount == undefined){
        		vm.contractInfo.amount = 0;
        	}
        	if(vm.contractInfo.taxRate == undefined){
        		vm.contractInfo.taxRate = 0;
        	}
        	if(vm.contractInfo.shareRate == undefined){
        		vm.contractInfo.shareRate = 0;
        	}
        	vm.contractInfo.taxes = vm.contractInfo.amount * vm.contractInfo.taxRate / 100;
        	vm.contractInfo.taxes = Math.round(vm.contractInfo.taxes * 100) / 100;
        	vm.contractInfo.shareCost = vm.contractInfo.amount * vm.contractInfo.shareRate / 100;
        	vm.contractInfo.shareCost = Math.round(vm.contractInfo.shareCost * 100) / 100;
        }
        function taxRateChanged(){
        	if(vm.contractInfo.amount == undefined){
        		vm.contractInfo.amount = 0;
        	}
        	if(vm.contractInfo.taxRate == undefined){
        		vm.contractInfo.taxRate = 0;
        	}
        	
        	vm.contractInfo.taxes = vm.contractInfo.amount * vm.contractInfo.taxRate / 100;
        	vm.contractInfo.taxes = Math.round(vm.contractInfo.taxes * 100)/100;
        }
        
        function taxesChanged(){
        	if(vm.contractInfo.amount == undefined){
        		vm.contractInfo.amount = 0;
        	}
        	if(vm.contractInfo.taxes == undefined){
        		vm.contractInfo.taxes = 0;
        	}
        	if(vm.contractInfo.amount == 0){
        		vm.contractInfo.taxRate = 0;
        	}else{
        		vm.contractInfo.taxRate = vm.contractInfo.taxes / vm.contractInfo.amount * 100;
        		vm.contractInfo.taxRate = Math.round(vm.contractInfo.taxRate * 100)/100;
        	}
        }
        function shareRateChanged(){
        	if(vm.contractInfo.amount == undefined){
        		vm.contractInfo.amount = 0;
        	}
        	if(vm.contractInfo.shareRate == undefined){
        		vm.contractInfo.shareRate = 0;
        	}
        	
        	vm.contractInfo.shareCost = vm.contractInfo.amount * vm.contractInfo.shareRate / 100;
        	vm.contractInfo.shareCost = Math.round(vm.contractInfo.shareCost * 100)/100;
        }
        
        function shareCostChanged(){
        	if(vm.contractInfo.amount == undefined){
        		vm.contractInfo.amount = 0;
        	}
        	if(vm.contractInfo.shareCost == undefined){
        		vm.contractInfo.shareCost = 0;
        	}
        	if(vm.contractInfo.amount == 0){
        		vm.contractInfo.shareRate = 0;
        	}else{
        		vm.contractInfo.shareRate = vm.contractInfo.shareCost / vm.contractInfo.amount * 100;
        		vm.contractInfo.shareRate = Math.round(vm.contractInfo.shareRate * 100)/100;
        	}
        }
        vm.datePickerOpenStatus.startDay = false;
        vm.datePickerOpenStatus.endDay = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
        //部门的处理
        var unsubscribe = $rootScope.$on('cpmApp:deptInfoSelected', function(event, result, dataType) {
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
