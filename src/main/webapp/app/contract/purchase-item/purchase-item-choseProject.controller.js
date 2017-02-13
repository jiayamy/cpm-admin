(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ChoseProjectsController', ChoseProjectsController);

    ChoseProjectsController.$inject = ['$scope', '$state', 'PurchaseItem', 'ParseLinks', 'AlertService', 'paginationConstants', 'entity','$uibModalInstance','$stateParams'];

    function ChoseProjectsController ($scope, $state, PurchaseItem, ParseLinks, AlertService, paginationConstants, entity,$uibModalInstance,$stateParams) {
        var vm = this;
        vm.itemsPerPage = paginationConstants.itemsPerPage;
        vm.close = close;
        vm.chose = chose;
        vm.clear = clear;
        vm.find = find;
        vm.searchPP = {};
        
        //搜索中的参数
        vm.haveSearch = null;
        vm.page = 1;
        //加载列表数据
        find();
        
        function onSuccess(data, headers) {
        	vm.links = ParseLinks.parse(headers('link'));
        	vm.totalItems = headers('X-Total-Count');
        	vm.queryCount = vm.totalItems;
        	vm.productPrices = handleData(data);
        }
        function onError(error) {
        	AlertService.error(error.data.message);
        }
        
        function handleData(data){
        	if(data.length > 0){
        		for(var i = 0; i< data.length ; i++){
        			if(data[i].type == 1){
        				data[i].typeName = "硬件";
        			}else if(data[i].type == 2){
        				data[i].typeName = "软件";
        			}
        			if(data[i].source == 2){
        				data[i].sourceName = "外部";
        			}else if(data[i].source == 1){
        				data[i].sourceName = "内部";
        			}
        		}
        	}
        	return data;
        }
        
        function find() {
<<<<<<< HEAD
        	console.log(vm.searchPP.selectName);
            if (!vm.searchPP.selectName){
                return vm.clear();
            }
            PurchaseItem.queryProductPrice({
        		selectName: entity.selectName,
                page: entity.page - 1,
=======
        	if(vm.searchPP.selectName == undefined){
        		vm.haveSearch = null;
        	}else{
        		vm.haveSearch = true;
        	}
        	PurchaseItem.queryProductPrice({
        		type:entity.type,
        		selectName: vm.searchPP.selectName,
                page: vm.page-1,
>>>>>>> 8817ed4caa94086bea503690b1faccfa9a0b16b5
                size: vm.itemsPerPage
            }, onSuccess, onError);
        }
        
        function clear() {
            vm.page = 1;
            vm.searchPP = {};
            find();
        }

        function close () {
            $uibModalInstance.dismiss('cancel');
        }
        
        function chose(productPrice){
        	vm.isSaving = true;
        	$scope.$emit('cpmApp:choseProject',productPrice);
            $uibModalInstance.dismiss('cancel');
            vm.isSaving = false;
            return;
        }
    }
})();
