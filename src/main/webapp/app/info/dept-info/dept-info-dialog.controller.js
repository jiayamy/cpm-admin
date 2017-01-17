(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('DeptInfoDialogController', DeptInfoDialogController);

    DeptInfoDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity','parentEntity', 'DeptInfo', 'DeptType'];

    function DeptInfoDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity,parentEntity, DeptInfo,DeptType) {
        var vm = this;

        vm.deptInfo = entity;
        if(vm.deptInfo.id == undefined){
        	vm.deptInfo.type = parentEntity.type;
        }
        vm.clear = clear;
        vm.save = save;
        vm.types = {};
        
        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }
        loadDeptType();
        function loadDeptType(){
        	DeptType.query({
            }, onSuccess, onError);
            
            function onSuccess(data, headers) {
            	vm.types = data;
            	if(vm.types && vm.types.length > 0){
        			var select = false;
        			for(var i = 0; i < vm.types.length; i++){
        				if(vm.deptInfo.type == vm.types[i].id){
        					vm.deptInfo.type = vm.types[i];
        					select = true;
        				}
        			}
        			if(!select){
        				vm.deptInfo.type = vm.contractInfos[0];
        			}
        		}
            }
            function onError(error) {
                AlertService.error(error.data.message);
            }
        }
        function save () {
            vm.isSaving = true;
            var deptInfo = {};
            deptInfo.id = vm.deptInfo.id;
            deptInfo.type = vm.deptInfo.type ? vm.deptInfo.type.id : "";
            deptInfo.parentId = vm.deptInfo.parentId;
            deptInfo.name = vm.deptInfo.name;
            
         	DeptInfo.update(deptInfo, onSaveSuccess, onSaveError);
        }

        function onSaveSuccess (result) {
            $scope.$emit('cpmApp:deptInfoUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }
    }
})();
