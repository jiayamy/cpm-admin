(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('UserManagementDialogController',UserManagementDialogController);

    UserManagementDialogController.$inject = ['$scope','$rootScope','$state','$stateParams',  'entity', 'User', 'JhiLanguageService','DeptInfo','previousState'];

    function UserManagementDialogController ($scope,$rootScope,$state,$stateParams, entity, User, JhiLanguageService,DeptInfo,previousState) {
        var vm = this;
        
        vm.previousState = previousState.name;
        vm.queryDept = previousState.queryDept;

        vm.authorities = ['ROLE_ADMIN','ROLE_USER','ROLE_TIMESHEET','ROLE_INFO','ROLE_INFO_BASIC','ROLE_INFO_USERCOST','ROLE_CONTRACT','ROLE_CONTRACT_BUDGET','ROLE_CONTRACT_COST','ROLE_CONTRACT_FINISH','ROLE_CONTRACT_INFO','ROLE_CONTRACT_PRODUCTPRICE','ROLE_CONTRACT_PURCHASE','ROLE_CONTRACT_RECEIVE','ROLE_CONTRACT_TIMESHEET','ROLE_CONTRACT_USER','ROLE_PROJECT','ROLE_PROJECT_COST','ROLE_PROJECT_FINISH','ROLE_PROJECT_INFO','ROLE_PROJECT_TIMESHEET','ROLE_PROJECT_USER','ROLE_STAT','ROLE_STAT_CONTRACT','ROLE_STAT_PROJECT'];

        vm.languages = null;
        vm.save = save;
        vm.user = entity;
        vm.genders = [{key:1,val:"男"},{key:2,val:"女"}];

        for(var j = 0; j < vm.genders.length ; j++){
    		if(entity.gender == vm.genders[j].key){
				vm.user.gender = vm.genders[j];
			}
    	}
        
        JhiLanguageService.getAll().then(function (languages) {
            vm.languages = languages;
        });

        function onSaveSuccess (result,headers) {
            vm.isSaving = false;
            if(headers("X-cpmApp-alert") == 'userManagement.updated'){
    			$state.go(vm.previousState);
    		}
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        function save () {
            vm.isSaving = true;
            
            var user = {};
            user.activated=vm.user.activated;
            user.authorities=vm.user.authorities;
            user.birthDay=vm.user.birthDay;
            user.birthYear=vm.user.birthYear;
            user.dept=vm.user.dept;
            user.deptId=vm.user.deptId;
            user.duty=vm.user.duty;
            user.email=vm.user.email;
            user.firstName=vm.user.firstName;
            user.gender=vm.user.gender ? vm.user.gender.key:1;
            user.grade=vm.user.grade;
            user.id=vm.user.id;
            user.isManager=vm.user.isManager;
            user.langKey=vm.user.langKey;
            user.lastName=vm.user.lastName;
            user.login=vm.user.login;
            user.password=vm.user.password;
            user.serialNum=vm.user.serialNum;
            user.telephone=vm.user.telephone;
        
            if (vm.user.id !== null) {
                User.update(user, onSaveSuccess, onSaveError);
            } else {
                User.save(user, onSaveSuccess, onSaveError);
            }
        }
        
        var unsubscribe = $rootScope.$on('cpmApp:deptInfoSelected', function(event, result) {
        	vm.user.deptId = result.objId;
        	vm.user.dept = result.name;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
