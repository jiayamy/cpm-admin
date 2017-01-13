(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ContractInfoDetailController', ContractInfoDetailController);

    ContractInfoDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'ContractInfo'];

    function ContractInfoDetailController($scope, $rootScope, $stateParams, previousState, entity, ContractInfo) {
        var vm = this;

        vm.contractInfo =handleData(entity);
        vm.previousState = previousState.name;
        console.log(vm.contractInfoName);
        
        function handleData(data){
			//type的枚举
			if(data.type==1){
				data.typeName="产品合同";
			}else if (data.type==2) {
				data.typeName="外包合同";
			}else if (data.type==3) {
				data.typeName="硬件合同";
			}else if (data.type==4) {
				data.typeName="公共成本";
			}
			//status的枚举
			if(data.status == 1){
				data.statusName = "可用";
			}else if(data.status == 2){
				data.statusName = "完成";
			}else if(data.status == 3){
				data.statusName = "删除";
			}
			//是否预立
			if(data.isPrepared == true){
				data.isPreparedName = "正式合同"
			}else if (data.isPrepared == false) {
				data.isPreparedName = "预立合同"
			}
			//是否外包
			if(data.isEpibolic == true){
				data.isEpibolicName = "外包"
			}else if (data.isEpibolic == false) {
				data.isEpibolicName = "非外包"
			}
        	return data;
        }
        //合同类型的枚举
        var unsubscribe = $rootScope.$on('cpmApp:contractInfoUpdate', function(event, result) {
        	console.log(result);
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
