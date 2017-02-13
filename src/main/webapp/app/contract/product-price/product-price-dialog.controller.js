(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ProductPriceDialogController', ProductPriceDialogController);

    ProductPriceDialogController.$inject = ['$timeout','$state', '$scope', '$stateParams', 'entity', 'ProductPrice','previousState'];

    function ProductPriceDialogController ($timeout,$state, $scope, $stateParams, entity, ProductPrice,previousState) {
        var vm = this;
        
        vm.previousState = previousState.name;
        vm.productPrice = entity;
        
        vm.datePickerOpenStatus = {};
        
        vm.openCalendar = openCalendar;
        
        vm.save = save;
        
        vm.types = [{ id: 1, name: '硬件' }, { id: 2, name: '软件' }];
        
        for(var i = 0; i < vm.types.length; i++){
        	if (vm.productPrice.type == vm.types[i].id) {
				vm.productPrice.type = vm.types[i];
			}
        }
        vm.sources = [{ id: 1, name: '内部' }, { id: 2, name: '外部' }];
        for(var i = 0; i < vm.sources.length; i++){
        	if (vm.productPrice.source == vm.sources[i].id) {
				vm.productPrice.source = vm.sources[i];
			}
        }

        function save () {
            vm.isSaving = true;
            
            var productPrice = {};
            productPrice.id = vm.productPrice.id;
            productPrice.name = vm.productPrice.name;
            productPrice.type = vm.productPrice.type ? vm.productPrice.type.id : "";
            productPrice.source = vm.productPrice.source ? vm.productPrice.source.id : "";
            productPrice.units = vm.productPrice.units;
            productPrice.price = vm.productPrice.price;
            
            if (vm.productPrice.id !== null) {
                ProductPrice.update(productPrice, onSaveSuccess, onSaveError);
            } else {
                ProductPrice.save(productPrice, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('cpmApp:productPriceUpdate', result);
            $state.go('product-price');
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.createTime = false;
        vm.datePickerOpenStatus.updateTime = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
