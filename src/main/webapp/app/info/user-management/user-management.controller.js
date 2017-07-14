(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('UserManagementController', UserManagementController);

    UserManagementController.$inject = ['Principal', 'User','WorkArea', 'ParseLinks', 'AlertService','$scope','$rootScope', '$state', 'pagingParams', 'paginationConstants', 'JhiLanguageService'];

    function UserManagementController(Principal, User,WorkArea, ParseLinks, AlertService,$scope,$rootScope, $state, pagingParams, paginationConstants, JhiLanguageService) {
        var vm = this;

        vm.currentAccount = null;
        vm.languages = null;
        vm.loadAll = loadAll;
        vm.setActive = setActive;
        vm.users = [];
        vm.page = 1;
        vm.totalItems = null;
        vm.clear = clear;
        vm.links = null;
        vm.loadPage = loadPage;
        vm.predicate = pagingParams.predicate;
        vm.reverse = pagingParams.ascending;
        vm.itemsPerPage = paginationConstants.itemsPerPage;
        vm.transition = transition;
        vm.search = search;
        vm.clear = clear;
        vm.setAllChecked = setAllChecked;
        vm.exportXls = exportXls;
        
        vm.searchQuery = {};
        vm.searchQuery.serialNum = pagingParams.serialNum;
        vm.searchQuery.lastName = pagingParams.lastName;
        vm.searchQuery.loginName = pagingParams.loginName;
        vm.searchQuery.deptId = pagingParams.deptId;
        vm.searchQuery.deptName = pagingParams.deptName;
        vm.searchQuery.workArea = pagingParams.workArea;
        vm.searchQuery.grade = pagingParams.grade;
        vm.searchQuery.duty = pagingParams.duty;
        vm.searchQuery.isManager = pagingParams.isManager;
        if (vm.searchQuery.serialNum == undefined&& vm.searchQuery.lastName == undefined
        		&& vm.searchQuery.loginName == undefined && vm.searchQuery.deptId == undefined
        		&& vm.searchQuery.workArea == undefined && vm.searchQuery.grade == undefined
        		 && vm.searchQuery.duty == undefined && vm.searchQuery.isManager == undefined
        		 ){
        	vm.haveSearch = null;
        }else{
        	vm.haveSearch = true;
        }
        
        vm.loadAll();
        
        loadWorkArea();
        function loadWorkArea(){
        	WorkArea.queryAll({},function onSuccess(data, headers) {
        		vm.allAreas = data;
        	});
        }
        
        JhiLanguageService.getAll().then(function (languages) {
            vm.languages = languages;
        });
        Principal.identity().then(function(account) {
            vm.currentAccount = account;
        });

        function setActive (user, isActivated) {
            user.activated = isActivated;
            User.update(user, function () {
                vm.loadAll();
                vm.clearUser();
            });
        }
        function setAllChecked(){
        	if(vm.users && vm.users.length > 0){
        		for(var i = 0; i < vm.users.length ; i++){
        			vm.users[i].isChecked = vm.allChecked;
        		}
        	}
        }
        User.queryAllGrade({},function onSuccess(data, headers) {
        	vm.grades = data;
    	});
        function loadAll () {
        	vm.allChecked = false;
            User.query({
            	loginName:pagingParams.loginName,
            	lastName:pagingParams.lastName,
            	serialNum:pagingParams.serialNum,
            	deptId:pagingParams.deptId,
            	workArea:pagingParams.workArea,
            	grade:pagingParams.grade,
            	duty:pagingParams.duty,
            	isManager:pagingParams.isManager,
                page: pagingParams.page - 1,
                size: vm.itemsPerPage,
                sort: sort()
            }, onSuccess, onError);
        }

        function onSuccess(data, headers) {
            var hiddenUsersSize = 0;
            for (var i in data) {
                if (data[i]['login'] === 'anonymoususer') {
                    data.splice(i, 1);
                    hiddenUsersSize++;
                }
            }
            vm.links = ParseLinks.parse(headers('link'));
            vm.totalItems = headers('X-Total-Count') - hiddenUsersSize;
            vm.queryCount = vm.totalItems;
            vm.page = pagingParams.page;
            vm.users = data;
        }

        function onError(error) {
            AlertService.error(error.data.message);
        }

        function clearUser () {
            vm.user = {
                id: null, login: null, firstName: null, lastName: null, email: null,
                activated: null, langKey: null, createdBy: null, createdDate: null,
                lastModifiedBy: null, lastModifiedDate: null, resetDate: null,
                resetKey: null, authorities: null
            };
        }

        function sort () {
            var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
            if (vm.predicate !== 'user.id') {
                result.push('user.id');
            }
            return result;
        }

        function loadPage (page) {
            vm.page = page;
            vm.transition();
        }

        function transition () {
            $state.transitionTo($state.$current, {
                page: vm.page,
                sort: vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc'),
                serialNum:vm.searchQuery.serialNum,
                lastName:vm.searchQuery.lastName,
                loginName:vm.searchQuery.loginName,
                deptId:vm.searchQuery.deptId,
                deptName:vm.searchQuery.deptName,
                workArea:vm.searchQuery.workArea,
                grade:vm.searchQuery.grade,
                duty:vm.searchQuery.duty,
                isManager:vm.searchQuery.isManager
            });
        }
        function search() {
        	if (vm.searchQuery.serialNum == undefined&& vm.searchQuery.lastName == undefined
            		&& vm.searchQuery.loginName == undefined&& vm.searchQuery.deptId == undefined
            		&& vm.searchQuery.workArea == undefined && vm.searchQuery.grade == undefined
            		&& vm.searchQuery.duty == undefined && vm.searchQuery.isManager == undefined
            		){
                return vm.clear();
            }
            vm.links = null;
            vm.page = 1;
            vm.predicate = 'user.id';
            vm.reverse = false;
            vm.haveSearch = true;
            vm.transition();
        }

        function clear() {
            vm.links = null;
            vm.page = 1;
            vm.predicate = 'user.id';
            vm.reverse = false;
            vm.searchQuery = {};
            vm.haveSearch = null;
            vm.transition();
        }
        
        var unsubscribe = $rootScope.$on('cpmApp:deptInfoSelected', function(event, result) {
        	vm.searchQuery.deptId = result.objId;
        	vm.searchQuery.deptName = result.name;
        });
        $scope.$on('$destroy', unsubscribe);
        
        function exportXls(){//导出Xls
        	var url = "api/users/exportXls";
        	var c = 0;
    		
    		var contractId = vm.searchQuery.contractId ? vm.searchQuery.contractId.key : "";
        	var serialNum = vm.searchQuery.serialNum;
        	var name = vm.searchQuery.name;
        	var status = vm.searchQuery.status ? vm.searchQuery.status.key : "";
        	
        	var loginName = vm.searchQuery.loginName;
        	var lastName = vm.searchQuery.lastName;
        	var serialNum = vm.searchQuery.serialNum;
        	var deptId = vm.searchQuery.deptId;
        	var workArea = vm.searchQuery.workArea;
        	var grade = vm.searchQuery.grade;
        	var duty = vm.searchQuery.duty;
        	var isManager = vm.searchQuery.isManager;
        	
			if(loginName){
				if(c == 0){
					c++;
					url += "?";
				}else{
					url += "&";
				}
				url += "loginName="+encodeURI(loginName);
			}
			if(lastName){
				if(c == 0){
					c++;
					url += "?";
				}else{
					url += "&";
				}
				url += "lastName="+encodeURI(lastName);
			}
			if(serialNum){
				if(c == 0){
					c++;
					url += "?";
				}else{
					url += "&";
				}
				url += "serialNum="+encodeURI(serialNum);
			}
			if(deptId){
				if(c == 0){
					c++;
					url += "?";
				}else{
					url += "&";
				}
				url += "deptId="+encodeURI(deptId);
			}
			if(workArea){
				if(c == 0){
					c++;
					url += "?";
				}else{
					url += "&";
				}
				url += "workArea="+encodeURI(workArea);
			}
			if(grade){
				if(c == 0){
					c++;
					url += "?";
				}else{
					url += "&";
				}
				url += "grade="+encodeURI(grade);
			}
			
			if(duty){
				if(c == 0){
					c++;
					url += "?";
				}else{
					url += "&";
				}
				url += "duty="+encodeURI(duty);
			}
			if(isManager != undefined){
				if(c == 0){
					c++;
					url += "?";
				}else{
					url += "&";
				}
				url += "isManager="+encodeURI(isManager);
			}
        	window.open(url);
        }
    }
})();
