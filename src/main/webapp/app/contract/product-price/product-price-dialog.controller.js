(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ProductPriceDialogController', ProductPriceDialogController);

    ProductPriceDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'ProductPrice'];

    function ProductPriceDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, ProductPrice) {
        var vm = this;

        vm.productPrice = entity;
    	if(vm.productPrice.source == 0){
    		vm.productPrice.source = { id: 0, name: '内部' }; 
    	}else if(vm.productPrice.source == 1){
    		vm.productPrice.source = { id: 1, name: '外部' };
    	}
    	if(vm.productPrice.type == 0){
    		vm.productPrice.type = { id: 0, name: '硬件' }; 
    	}else if(vm.productPrice.type == 1){
    		vm.productPrice.type = { id: 1, name: '软件' };
    	}
        
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.types = [{ id: 0, name: '硬件' }, { id: 1, name: '软件' }];
        vm.sources = [{ id: 0, name: '内部' }, { id: 1, name: '外部' }];

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            var post = vm.productPrice;
            if(vm.productPrice.source){
            	if(vm.productPrice.source.id == 0){
            		vm.productPrice.source = 0;
                }else if(post.source.id == 1){
                	vm.productPrice.source = 1;
                }
            }
            if(vm.productPrice.type){
            	if(vm.productPrice.type.id == 0){
            		vm.productPrice.type = 0;
                }else if(post.type.id == 1){
                	vm.productPrice.type = 1;
                }
            }
            if (vm.productPrice.id !== null) {
                ProductPrice.update(vm.productPrice, onSaveSuccess, onSaveError);
            } else {
                ProductPrice.save(vm.productPrice, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('cpmApp:productPriceUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
            if(vm.productPrice.source == 0){
        		vm.productPrice.source = { id: 0, name: '内部' }; 
        	}else if(vm.productPrice.source == 1){
        		vm.productPrice.source = { id: 1, name: '外部' };
        	}
            if(vm.productPrice.type == 0){
        		vm.productPrice.type = { id: 0, name: '硬件' }; 
        	}else if(vm.productPrice.type == 1){
        		vm.productPrice.type = { id: 1, name: '软件' };
        	}
        }

        vm.datePickerOpenStatus.createTime = false;
        vm.datePickerOpenStatus.updateTime = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
