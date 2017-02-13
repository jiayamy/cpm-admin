(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ChoseProjectsController', ChoseProjectsController);

    ChoseProjectsController.$inject = ['$scope', '$state', 'PurchaseItem', 'ParseLinks', 'AlertService', 'paginationConstants', 'entity','$uibModalInstance','$stateParams'];

    function ChoseProjectsController ($scope, $state, PurchaseItem, ParseLinks, AlertService, paginationConstants, entity,$uibModalInstance,$stateParams) {
        var vm = this;
        vm.loadPage = loadPage;
        vm.transition = transition;
        vm.itemsPerPage = paginationConstants.itemsPerPage;
        vm.close = close;
        vm.chose = chose;
        vm.clear = clear;
        vm.find = find;
        vm.searchPP = {};
        
        //搜索中的参数
        vm.searchPP.selectName = entity.selectName;
        console.log(entity.selectName);
        if (!vm.searchPP.selectName) {
			vm.haveSearch = null;
		}else {
			vm.haveSearch = true;
		}
        
        //加载列表数据
        loadAll();
        function loadAll () {
        	if(!entity.selectName){
        		entity.selectName = "";
        	}
        	PurchaseItem.queryProductPrice({
        		selectName: entity.selectName,
                page: entity.page - 1,
                size: vm.itemsPerPage
            }, onSuccess, onError);
            
            function onSuccess(data, headers) {
            	vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
                vm.queryCount = vm.totalItems;
                vm.productPrices = handleData(data);
                vm.page = entity.page;
            }
            function onError(error) {
                AlertService.error(error.data.message);
            }
        }
        
        function handleData(data){
        	if(data.length > 0){
        		for(var i = 0; i< data.length ; i++){
        			if(data[i].type == 0){
        				data[i].typeName = "硬件";
        			}else if(data[i].type == 1){
        				data[i].typeName = "软件";
        			}
        			if(data[i].source == 0){
        				data[i].sourceName = "外部";
        			}else if(data[i].source == 1){
        				data[i].sourceName = "内部";
        			}
        		}
        	}
        	return data;
        }
        
        function loadPage(page) {
            vm.page = page;
            vm.transition();
        }

        function transition() {
            $state.transitionTo($state.$current, {
                page: vm.page,
                selectName: vm.searchPP.selectName
            });
        }
        
        function find() {
        	console.log(vm.searchPP.selectName);
            if (!vm.searchPP.selectName){
                return vm.clear();
            }
            PurchaseItem.queryProductPrice({
        		selectName: entity.selectName,
                page: entity.page - 1,
                size: vm.itemsPerPage
            }, onSuccess, onError);
        }
        
        function clear() {
            vm.links = null;
            vm.page = 1;
            vm.searchPP = {};
            vm.haveSearch = null;
            vm.transition();
        }

        function close () {
            $uibModalInstance.dismiss('cancel');
        }
        
        
        function chose(productPrice){
        	vm.isSaving = true;
        	$scope.$emit('cpmApp:choseProject',productPrice);
            $uibModalInstance.close(productPrice);
            vm.isSaving = false;
            return;
        }
    }
})();
