(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('PurchaseItemDialogController', PurchaseItemDialogController);

    PurchaseItemDialogController.$inject = ['$timeout','$scope', 'entity', 'PurchaseItem','ProjectInfo','previousState','$state'];

    function PurchaseItemDialogController ($timeout, $scope, entity, PurchaseItem,ProjectInfo,previousState,$state) {
        var vm = this;

        vm.purchaseItem = entity;
        console.log(vm.purchaseItem.budgetName);
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
        			for(var i = 0; i < vm.contractInfos.length; i++){
        				if(entity.contractId == vm.contractInfos[i].key){
        					vm.purchaseItem.contractId = vm.contractInfos[i];
        				}
        			}
        		}
        	},
        	function(error){
        		AlertService.error(error.data.message);
        	});
        }
        
        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function save () {
            vm.isSaving = true;
            var purchaseItem = {};
            purchaseItem.id = vm.purchaseItem.id;
            
            purchaseItem.contractId = vm.purchaseItem.contractId;
            purchaseItem.contractName = vm.purchaseItem.contractName;
            purchaseItem.contractNum = vm.purchaseItem.contractNum;
            purchaseItem.contractName = vm.purchaseItem.contractName;
            
            purchaseItem.budgetId = vm.purchaseItem.budgetId;
            purchaseItem.budgetName = vm.purchaseItem.budgetName;
            
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
      
            PurchaseItem.update(purchaseItem, onSaveSuccess);
        }

        function onSaveSuccess (result) {
            	$state.go('purchase-item');
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
