(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('PurchaseItemDialogController', PurchaseItemDialogController);

    PurchaseItemDialogController.$inject = ['$timeout','$scope', 'entity', 'PurchaseItem','ProjectInfo','previousState','$state'];

    function PurchaseItemDialogController ($timeout, $scope, entity, PurchaseItem,ProjectInfo,previousState,$state) {
        var vm = this;

        vm.purchaseItem = entity;
        vm.contractChanged = contractChanged;
        vm.budgetChanged = budgetChanged;
        
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        
        vm.previousState = previousState.name;
        vm.types = [{ key: 1, val: '硬件'},{ key: 2, val: '软件'}];
        
        for(var j = 0; j < vm.types.length; j++){
        	if(vm.purchaseItem.type == vm.types[j].key){
        		vm.purchaseItem.type = vm.types[j];
        	}
        }
        vm.sources = [{ key: 1, val: '内部'},{ key: 2, val: '外部'}];
        
        for(var j = 0; j < vm.sources.length; j++){
        	if(vm.purchaseItem.source == vm.sources[j].key){
        		vm.purchaseItem.source = vm.sources[j];
        	}
        }
        
        vm.contractInfos = [];
        loadContract();
        function loadContract(){
        	ProjectInfo.queryUserContract({
        		
        	},
        	function(data, headers){
        		vm.contractInfos = data;
        		if(vm.contractInfos && vm.contractInfos.length > 0){
        			var select = false;
        			for(var i = 0; i < vm.contractInfos.length; i++){
        				if(entity.contractId == vm.contractInfos[i].key){
        					vm.purchaseItem.contractId = vm.contractInfos[i];
        					select = true;
        				}
        			}
        			if (!select) {
						vm.purchaseItem.contractId = vm.contractInfos[0];
					}
        			contractChanged();
        		}
        	},
        	function(error){
        		AlertService.error(error.data.message);
        	});
        }
        //加载采购单
        function contractChanged(){
        	var contractId = '';
        	
        	if(vm.purchaseItem && vm.purchaseItem.contractId && vm.purchaseItem.contractId.key){
        		contractId = vm.purchaseItem.contractId.key;
        	}else if(vm.purchaseItem && vm.purchaseItem.contractId){
        		contractId = vm.purchaseItem.contractId;
        	}else{
        		vm.contractBudgets = [];
        		return;
        	}
        	ProjectInfo.queryUserContractBudget({
        		contractId:contractId
	        	},
	    		function(data, headers){
	        		vm.contractBudgets = data;
	        		if(vm.contractBudgets && vm.contractBudgets.length > 0){
	        			var select = false;
	        			for(var i = 0; i < vm.contractBudgets.length; i++){
	        				if(entity.budgetId == vm.contractBudgets[i].key){
	        					vm.purchaseItem.budgetId = vm.contractBudgets[i];
	        					select = true;
	        				}
	        			}
	        			if(!select){
	        				vm.purchaseItem.budgetId = vm.contractBudgets[0];
	        			}
	        			budgetChanged();
	        		}
	        	},
	        	function(error){
	        		AlertService.error(error.data.message);
	        		vm.contractBudgets = [];
	        	}
	        );
        }
        function budgetChanged(){
        	if(vm.purchaseItem.budgetId && vm.purchaseItem.budgetId.key){
        		vm.purchaseItem.budgetOriginal = vm.purchaseItem.budgetId.p2;
        	}else{
        		vm.purchaseItem.budgetOriginal = vm.purchaseItem.budgetOriginal;
        	}
        }
        function save () {
            vm.isSaving = true;
            var purchaseItem = {};
            purchaseItem.id = vm.purchaseItem.id;
            
            purchaseItem.contractId = vm.purchaseItem.contractId;
            
            purchaseItem.budgetId = vm.purchaseItem.budgetId;
            
            purchaseItem.name = vm.purchaseItem.name;
            purchaseItem.quantity = vm.purchaseItem.quantity;
            purchaseItem.price = vm.purchaseItem.price;
            purchaseItem.units = vm.purchaseItem.units;
            purchaseItem.source = vm.purchaseItem.source;
            purchaseItem.purchaser = vm.purchaseItem.purchaser;
            purchaseItem.totalAmount = vm.purchaseItem.totalAmount;
            purchaseItem.type = vm.purchaseItem.type;
            
            if(purchaseItem.contractId && purchaseItem.contractId.key){
            	purchaseItem.contractId = purchaseItem.contractId.key;
            }
            
            if (purchaseItem.type) {
            	purchaseItem.type = purchaseItem.type.key;
            }
            
            if (purchaseItem.source) {
            	purchaseItem.source = purchaseItem.source.key;
			}
          //校验对象
            //预算和合同是否一致
            if(purchaseItem.contractId && purchaseItem.contractId.key){
            	purchaseItem.contractId = purchaseItem.contractId.key;
            }
            if(purchaseItem.budgetId && purchaseItem.budgetId.key){
            	if(purchaseItem.budgetId.p1 == purchaseItem.contractId){
            		purchaseItem.budgetId = purchaseItem.budgetId.key;
            	}else{
            	//	AlertService.error("cpmApp.purchaseItem.save.budgetError");
            		vm.isSaving = false;
            		return;
            	}
            }
            PurchaseItem.update(purchaseItem, onSaveSuccess,onSaveError);
        }
        function onSaveSuccess (result) {
            	$state.go('purchase-item');
            	vm.isSaving = false;
        	}
        
        function onSaveError (result) {
            vm.isSaving = false;
        }
        
        vm.priceChanged = priceChanged;
        vm.quantityChanged = quantityChanged;
        vm.totalAmountChanged = totalAmountChanged;
        
        function priceChanged(){
        	if (vm.purchaseItem.quantity == undefined) {
				vm.purchaseItem.quantity = 0;
			}
        	if (vm.purchaseItem.price == undefined) {
				vm.purchaseItem.price = 0;
			}
        	if (vm.purchaseItem.totalAmount == undefined) {
				vm.purchaseItem.totalAmount = 0;
			}
        	vm.purchaseItem.totalAmount = vm.purchaseItem.quantity * vm.purchaseItem.price;
        }
        
        function quantityChanged(){
        	if (vm.purchaseItem.quantity == undefined) {
				vm.purchaseItem.quantity = 0;
			}
        	if (vm.purchaseItem.price == undefined) {
				vm.purchaseItem.price = 0;
			}
        	if (vm.purchaseItem.totalAmount == undefined) {
				vm.purchaseItem.totalAmount = 0;
			}
        	vm.purchaseItem.totalAmount = vm.purchaseItem.quantity * vm.purchaseItem.price;
        }
        
        function totalAmountChanged(){
        	if (vm.purchaseItem.quantity == undefined) {
				vm.purchaseItem.quantity = 0;
			}
        	if (vm.purchaseItem.price == undefined) {
				vm.purchaseItem.price = 0;
			}
        	if (vm.purchaseItem.totalAmount == undefined) {
				vm.purchaseItem.totalAmount = 0;
			}
        	vm.purchaseItem.quantity = Math.floor(vm.purchaseItem.totalAmount / vm.purchaseItem.price);
        }

        vm.datePickerOpenStatus.createTime = false;
        vm.datePickerOpenStatus.updateTime = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
