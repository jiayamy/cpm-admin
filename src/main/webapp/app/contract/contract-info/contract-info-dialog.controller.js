(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ContractInfoDialogController', ContractInfoDialogController);

    ContractInfoDialogController.$inject = ['previousState','$rootScope','$timeout', '$scope', '$stateParams','entity', 'ContractInfo','$state','AlertService','ParseLinks','OutsourcingUser'];

    function ContractInfoDialogController (previousState,$rootScope,$timeout, $scope, $stateParams, entity, ContractInfo, $state,AlertService,ParseLinks,OutsourcingUser) {
        var vm = this;
        
        vm.contractInfo = entity;
        vm.outsourcingUser = entity;
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
        
        //点击外包合同时，给ng-chnage赋值
        if (vm.contractInfo.id != null) {
        	vm.outsourcingUser.rank = "高级";
        	vm.outsourcingUser.offer = 1;
        	vm.outsourcingUser.targetAmount = 1;
        	if (vm.contractInfo.type == 2 || vm.contractInfo.type.key == 2) {
    			vm.showTable = true;
    			ContractInfo.queryOutsourcingUser({
    	    		contractId:entity.id
    	         }, onSuccess, onError);
    			
    			function onSuccess(data, headers) {
                    vm.outsourcingUsers = data;
                }
                function onError(error) {
                    AlertService.error(error.data.message);
                }
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
        	
        	var outsourcingUser = {};
        	outsourcingUser.rank = vm.outsourcingUser.rank;
        	outsourcingUser.targetAmount = vm.outsourcingUser.targetAmount;
        	outsourcingUser.offer = vm.outsourcingUser.offer;
        	var num = GetRandomNum(1,100);
        	var randomDate = getNowFormatDate();
        	contractInfo.mark = num + "_" + randomDate;
        	outsourcingUser.mark = num + "_" + randomDate;
        	
        	var contractInfoVo = {};
        	contractInfoVo.contractInfo = contractInfo;
        	if (contractInfo.type == 2) {
        		contractInfoVo.outsourcingUser = outsourcingUser;
			}
        	ContractInfo.update(contractInfoVo,
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

        vm.amountChanged = amountChanged;
        vm.taxRateChanged = taxRateChanged;
        vm.taxesChanged = taxesChanged;
        vm.shareRateChanged = shareRateChanged;
        vm.shareCostChanged = shareCostChanged;
        vm.changeType = changeType;
        
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
        
        function changeType(){
        	if (vm.contractInfo.type.key == 2 && vm.contractInfo.type.val == "外包" && entity.id == null) {
				vm.showCase = true;
			}else {
				vm.showCase = false;
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
        
        //生成唯一标识中的组成部分
        function GetRandomNum(Min,Max)
        {  
        var Range = Max - Min;  
        var Rand = Math.random();  
        return(Min + Math.round(Rand * Range));  
        }  
        
        
        function getNowFormatDate() { 
            var time = new Date(); 
            var month = time.getMonth() + 1; 
            var date = time.getDate(); 
            var hours = time.getHours(); 
            var minutes = time.getMinutes(); 
            var seconds = time.getSeconds(); 
            var milliseconds = time.getMilliseconds(); 
            if (month >= 1 && month <= 9) { 
                month = "0" + month; 
            } 
            if (date >= 0 && date <= 9) { 
               date = "0" + date; 
            } 
            if (hours >= 0 && hours <= 9) { 
               hours = "0" + hours; 
            } 
            if (minutes >= 0 && minutes <= 9) { 
               minutes = "0" + minutes; 
            } 
            if (seconds >= 0 && seconds <= 9) { 
               seconds = "0" + seconds; 
            } 
            if (milliseconds >= 0 && milliseconds <= 9) { 
               milliseconds = "00" + milliseconds; 
          }else if (milliseconds >= 10 && milliseconds <= 99) { 
             milliseconds = "0" + milliseconds; 
          } 
           
            var currentdate = time.getFullYear() + month + date 
                  + hours + minutes + seconds + milliseconds; 
            return currentdate; 
        };
    }
})();
