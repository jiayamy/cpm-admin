(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('UserManagementDialogController',UserManagementDialogController);

    UserManagementDialogController.$inject = ['$stateParams',  'entity', 'User', 'JhiLanguageService','DeptInfo'];

    function UserManagementDialogController ($stateParams, entity, User, JhiLanguageService,DeptInfo) {
        var vm = this;
        
        vm.previousState = previousState.name;
        vm.queryDept = previousState.queryDept;

        vm.authorities = ['ROLE_ADMIN','ROLE_USER','ROLE_TIMESHEET','ROLE_INFO','ROLE_INFO_BASIC','ROLE_INFO_USERCOST','ROLE_CONTRACT','ROLE_CONTRACT_BUDGET','ROLE_CONTRACT_COST','ROLE_CONTRACT_FINISH','ROLE_CONTRACT_INFO','ROLE_CONTRACT_PRODUCTPRICE','ROLE_CONTRACT_PURCHASE','ROLE_CONTRACT_RECEIVE','ROLE_CONTRACT_TIMESHEET','ROLE_CONTRACT_USER','ROLE_PROJECT','ROLE_PROJECT_COST','ROLE_PROJECT_FINISH','ROLE_PROJECT_INFO','ROLE_PROJECT_TIMESHEET','ROLE_PROJECT_USER','ROLE_STAT','ROLE_STAT_CONTRACT','ROLE_STAT_PROJECT'];
        vm.clear = clear;
        vm.languages = null;
        vm.save = save;
        vm.user = entity;
        
        loadDept();
        function loadDept(){
        	if(entity.selectType == undefined){
         		entity.selectType = "0";
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
        }
     }
        function onSuccess(data, headers) {
        	vm.deptInfos = data;
        	for(var i = 0; i < vm.deptInfos.length; i++){
                if(vm.deptInfos[i].children && vm.deptInfos[i].children.length !=0){
                	vm.deptInfos[i].showChild = true;
                }
            }
        }

        JhiLanguageService.getAll().then(function (languages) {
            vm.languages = languages;
        });


        function onSaveSuccess (result) {
            vm.isSaving = false;
            $scope.$emit('cpmApp:contractBudgetUpdate', result);
            $state.go('user-management');
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        function save () {
            vm.isSaving = true;
            if (vm.user.id !== null) {
                User.update(vm.user, onSaveSuccess, onSaveError);
            } else {
                User.save(vm.user, onSaveSuccess, onSaveError);
            }
        }
    }
})();
