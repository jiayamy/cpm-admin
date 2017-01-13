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
        vm.types = [{ id: 1, name: '产品合同' }, { id: 2, name: '外包合同' },{ id: 3, name: '硬件合同' },{ id: 4, name: '公共成本' }];
        vm.statuses =[{ id: 1, name: '可用' }, { id: 2, name: '完成' },{ id: 3, name: '删除' }];

        function save () {
        	vm.isSaving = true;
        	console.log(vm.contractInfo);
        	var contractInfo = {};
        	contractInfo.id = vm.contractInfo.id;
        	contractInfo.serialNum = vm.contractInfo.serialNum;
        	contractInfo.name = vm.contractInfo.name;
        	contractInfo.amount = vm.contractInfo.amount;
        	
        	contractInfo.type = vm.contractInfo.type ? vm.contractInfo.type.id : "";
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
        var unsubscribe = $rootScope.$on('cpmApp:deptInfoSelected', function(event, result) {
        	vm.contractInfo.salesman = result.name;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
