(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('DeptInfoQueryController', DeptInfoQueryController);

    DeptInfoQueryController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance','AlertService','entity', 'DeptInfo'];

    function DeptInfoQueryController ($timeout, $scope, $stateParams, $uibModalInstance,AlertService,entity,DeptInfo) {
        var vm = this;
        console.log(entity);

        vm.clear = clear;
        
        vm.isChildShowed = isChildShowed;
        vm.showOrHiddenChild = showOrHiddenChild;
        vm.selectNode = selectNode;
        
        loadAll();
        
        function loadAll () {
        	if(entity.selectType == undefined){
        		entity.selectType = "0";
        	}
        	if(entity.showChild == undefined){
        		entity.showChild = "true";
        	}
            DeptInfo.getDeptAndUserTree({
            	selectType:entity.selectType,
            	showChild:entity.showChild
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
        	$scope.$emit('cpmApp:deptInfoSelected', node);
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
