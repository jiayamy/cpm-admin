(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('DeptInfoQueryController', DeptInfoQueryController);

    DeptInfoQueryController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance','AlertService','entity', 'DeptInfo'];

    function DeptInfoQueryController ($timeout, $scope, $stateParams, $uibModalInstance,AlertService,entity,DeptInfo) {
        var vm = this;
        vm.clear = clear;
        
        vm.isChildShowed = isChildShowed;
        vm.showOrHiddenChild = showOrHiddenChild;
        vm.selectNode = selectNode;
        
        loadAll();
        
        //entity 中4个参数 selectType showChild dataType showUser
        //selectType 0:不选择，1：选择所有，2：只选择部门，3：只选择用户，默认不选择
        //showChild true/false。是否展示所有的子部门，默认展示
        //dataType 选择后返回参数，自己定义
        //showUser 是否显示用户，默认显示
        function loadAll () {
        	if(entity.selectType == undefined){
        		entity.selectType = "0";
        	}
        	if(entity.showChild == undefined){
        		entity.showChild = "true";
        	}
        	if(entity.showUser == undefined){
        		entity.showUser = "true";
        	}
            DeptInfo.getDeptAndUserTree({
            	selectType:entity.selectType,
            	showChild:entity.showChild,
            	showUser:entity.showUser
            }, onSuccess, onError);
            
            function onSuccess(data, headers) {
            	vm.deptInfos = data;
            	for(var i = 0; i < vm.deptInfos.length; i++){
                    if(vm.deptInfos[i].children && vm.deptInfos[i].children.length !=0){
                    	vm.deptInfos[i].showChild = true;
                    }
                }
            }
            function onError(error) {
                AlertService.error(error.data.message);
            }
        }
        function selectNode(node){
        	vm.isSaving = true;
        	$scope.$emit('cpmApp:deptInfoSelected', node,entity.dataType);
            $uibModalInstance.close(node);
            vm.isSaving = false;
            return;
        }
        function isChildShowed(node){
        	if(node.children && node.children.length != 0 && node.showChild){
                return true;
            }
            return false;
        }
        
        function showOrHiddenChild(node){
        	if(node.children && node.children.length != 0){
                node.showChild = !node.showChild;
            }
        	return;
        }
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }
    }
})();
