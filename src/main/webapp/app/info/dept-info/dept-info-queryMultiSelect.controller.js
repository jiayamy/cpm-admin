(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('DeptInfoQueryMultiSelectController', DeptInfoQueryMultiSelectController);

    DeptInfoQueryMultiSelectController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance','AlertService','entity', 'DeptInfo'];

    function DeptInfoQueryMultiSelectController ($timeout, $scope, $stateParams, $uibModalInstance,AlertService,entity,DeptInfo) {
        var vm = this;
        vm.clear = clear;
        
        vm.isChildShowed = isChildShowed;
        vm.showOrHiddenChild = showOrHiddenChild;
        vm.selectNode = selectNode;
        vm.searchQuery = {};
        vm.searchQuery.name = "";
        vm.haveSearch = false;
        vm.searchForm = searchForm;
        vm.clearForm = clearForm;
        vm.supportMultiSelect = true;//是否支持多选
        
        function searchForm(){
        	if(vm.searchQuery.name == undefined){
        		vm.clearForm();
        		return;
        	}
        	vm.haveSearch = true;
        	loadAll();
        }
        function clearForm(){
        	vm.haveSearch = false;
        	vm.searchQuery.name = "";
        	loadAll();
        }
        loadAll();
        //entity 中4个参数 selectType showChild dataType showUser
        //selectType 0:不选择，1：选择所有，2：只选择部门，3：只选择用户，默认不选择
        //showChild true/false。是否展示所有的子部门，默认展示
        //dataType 选择后返回参数，自己定义
        //showUser 是否显示用户，默认显示
     	//showDel 显示删除的
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
            	showUser:entity.showUser,
            	name:vm.searchQuery.name,
            	showDel:vm.searchQuery.showDel
            }, onSuccess, onError);
            
            function onSuccess(data, headers) {
            	vm.deptInfos = data;
            	//第2级先不展开
            	for(var i = 0; i < vm.deptInfos.length; i++){
                    if(vm.deptInfos[i].children && vm.deptInfos[i].children.length !=0){
                    	vm.deptInfos[i].showChild = true;
                    	if(!vm.haveSearch){
                    		hiddenSpecifiedNode(vm.deptInfos[i].children,1,2);
                    	}
                    }
                }
            }
            //2级以后的都隐藏
            function hiddenSpecifiedNode(node,currDeptIndex,showDeptIndex){
            	var nextDept = currDeptIndex + 1;
        		for(var i = 0; i < node.length; i++){
        			if(currDeptIndex >= showDeptIndex){
        				node[i].showChild = false;
        			}
        			if(node[i].children && node[i].children.length !=0){
        				hiddenSpecifiedNode(node[i].children,nextDept,showDeptIndex);
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
        	if(node.children && node.showChild){
                return true;
            }
            return false;
        }
        
        function showOrHiddenChild(node){
        	if(node.children){
                node.showChild = !node.showChild;
            }
        	return;
        }
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }
        
        vm.multiNode = [];
        vm.multiSelectNode = multiSelectNode;
        function multiSelectNode(node){
        	var isAdd = true;
        	for(var i=0;i<vm.multiNode.length;i++){
        		if(vm.multiNode[i].objId == node.objId){
        			isAdd = false;
        		}
        	}
        	if(isAdd){
        		vm.multiNode.push(node);
        	}
        }
        vm.okSelect= okSelect;
        function okSelect(){
        	var boxId = [];
        	var result = [];
//        	var obj = document.getElementsByTagName("input");
        	var obj = $("input:checked");
        	for(var i = 0;i<obj.length;i++){
        		boxId.push(obj[i].id);
        	}
        	for(var i = 0;i<boxId.length;i++){
        		for(var j=0;j<vm.multiNode.length;j++){
        			if(vm.multiNode[j].objId == boxId[i]){
        				result.push(vm.multiNode[j]);
        			}
        		}
        	}
        	$scope.$emit('cpmApp:deptInfoSelected', result,entity.dataType);
            $uibModalInstance.close(result);
            return ;
        }
    }
})();
