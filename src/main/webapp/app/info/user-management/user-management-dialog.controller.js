(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('UserManagementDialogController',UserManagementDialogController);

    UserManagementDialogController.$inject = ['$scope','$rootScope','$state','$stateParams',  'entity', 'User', 'JhiLanguageService','DeptInfo','previousState','WorkArea'];

    function UserManagementDialogController ($scope,$rootScope,$state,$stateParams, entity, User, JhiLanguageService,DeptInfo,previousState,WorkArea) {
        var vm = this;
        
        vm.previousState = previousState.name;
        vm.queryDept = previousState.queryDept;

        vm.languages = null;
        vm.save = save;
        vm.user = entity;
        
        vm.genders = [{key:1,val:"男"},{key:2,val:"女"}];
        for(var j = 0; j < vm.genders.length ; j++){
    		if(entity.gender == vm.genders[j].key){
				vm.user.gender = vm.genders[j];
			}
    	}
        
        loadWorkArea();
        function loadWorkArea(){
        	WorkArea.queryAll({},function onSuccess(data, headers) {
        		vm.allAreas = data;
        		if(vm.allAreas && vm.allAreas.length > 0){
        			if(vm.user.id == undefined){
        				vm.user.workArea = vm.allAreas[0];
        			}
        		}
        	});
        }
        User.queryAllGrade({},function onSuccess(data, headers) {
        	vm.grades = data;
    	});
        User.queryAllAuthorities({},function onSuccess(data, headers) {
        	vm.authorities = data;
    		if(data && data.length > 0){
    			if(vm.user.authorities && vm.user.authorities.length > 0){
    				var authorities = [];
    				for(var i = 0; i < vm.user.authorities.length; i++){
    					for(var j = 0; j < vm.authorities.length; j++){
    						if(vm.authorities[j].name == vm.user.authorities[i]){
    							authorities.push(vm.authorities[j]);
    							break;
    						}
    					}
    				}
    				vm.user.authorities = authorities;
    			}
    		}
    	});
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
            user.workArea = vm.user.workArea;
            
            if(user.authorities && user.authorities.length > 0 && user.authorities[0].name){
            	var authorities = [];
            	for(var i = 0; i < user.authorities.length ; i++){
            		authorities.push(user.authorities[i].name);
            	}
            	user.authorities = authorities;
            }
            
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
