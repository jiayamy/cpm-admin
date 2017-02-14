(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ContractInfoDetailController', ContractInfoDetailController);

    ContractInfoDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'ContractInfo'];

    function ContractInfoDetailController($scope, $rootScope, $stateParams, previousState, entity, ContractInfo) {
        var vm = this;
        
        vm.types = [{ key: 1, val: '产品' }, { key: 2, val: '外包' },{ key: 3, val: '硬件' },{ key: 4, val: '公共成本' }
        ,{ key: 5, val: '项目' },{ key: 6, val: '推广' },{ key: 7, val: '其他' }];
        vm.statuss = [{ key: 1, val: '进行中'}, { key: 2, val: '已完成'}, { key: 3, val: '已删除'}];

        vm.contractInfo = handleData(entity);
        
        vm.previousState = previousState.name;
        
        
        function handleData(data){
			for(var j = 0; j < vm.types.length; j++){
	        	if(data.type == vm.types[j].key){
	        		data.typeName = vm.types[j].val;
	        	}
	        }
	        for(var j = 0; j < vm.statuss.length; j++){
	        	if(data.status == vm.statuss[j].key){
	        		data.statusName = vm.statuss[j].val;
	        	}
	        }
	        if(data.isPrepared){
	        	data.isPreparedName = "预立合同";
	        }else{
	        	data.isPreparedName = "正式合同";
	        }
	        if(data.isEpibolic){
	        	data.isEpibolicName = "外部合同";
	        }else{
	        	data.isEpibolicName = "内部合同";
	        }
	        
        	return data;
        }
    }
})();
