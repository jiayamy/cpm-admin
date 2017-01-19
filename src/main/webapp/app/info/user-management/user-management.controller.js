(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('UserManagementController', UserManagementController);

    UserManagementController.$inject = ['Principal', 'User','WorkArea', 'ParseLinks', 'AlertService','$scope','$rootScope', '$state', 'pagingParams', 'paginationConstants', 'JhiLanguageService'];

    function UserManagementController(Principal, User,WorkArea, ParseLinks, AlertService,$scope,$rootScope, $state, pagingParams, paginationConstants, JhiLanguageService) {
        var vm = this;

        vm.authorities = ['ROLE_ADMIN','ROLE_USER','ROLE_TIMESHEET','ROLE_INFO','ROLE_INFO_BASIC','ROLE_INFO_USERCOST','ROLE_CONTRACT','ROLE_CONTRACT_BUDGET','ROLE_CONTRACT_COST','ROLE_CONTRACT_FINISH','ROLE_CONTRACT_INFO','ROLE_CONTRACT_PRODUCTPRICE','ROLE_CONTRACT_PURCHASE','ROLE_CONTRACT_RECEIVE','ROLE_CONTRACT_TIMESHEET','ROLE_CONTRACT_USER','ROLE_PROJECT','ROLE_PROJECT_COST','ROLE_PROJECT_FINISH','ROLE_PROJECT_INFO','ROLE_PROJECT_TIMESHEET','ROLE_PROJECT_USER','ROLE_STAT','ROLE_STAT_CONTRACT','ROLE_STAT_PROJECT'];
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
        
        vm.searchQuery = {};
        vm.searchQuery.serialNum = pagingParams.serialNum;
        vm.searchQuery.lastName = pagingParams.lastName;
        vm.searchQuery.loginName = pagingParams.loginName;
        vm.searchQuery.deptId = pagingParams.deptId;
        vm.searchQuery.deptName = pagingParams.deptName;
        vm.searchQuery.workArea = pagingParams.workArea;
        if (vm.searchQuery.serialNum == undefined&& vm.searchQuery.lastName == undefined
        		&& vm.searchQuery.loginName == undefined && vm.searchQuery.deptId == undefined
        		&& vm.searchQuery.workArea == undefined){
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

        function loadAll () {
            User.query({
            	loginName:pagingParams.loginName,
            	lastName:pagingParams.lastName,
            	serialNum:pagingParams.serialNum,
            	deptId:pagingParams.deptId,
            	workArea:pagingParams.workArea,
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
                workArea:vm.searchQuery.workArea
            });
        }
        function search() {
        	if (vm.searchQuery.serialNum == undefined&& vm.searchQuery.lastName == undefined
            		&& vm.searchQuery.loginName == undefined&& vm.searchQuery.deptId == undefined
            		&& vm.searchQuery.workArea == undefined){
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
    }
})();
