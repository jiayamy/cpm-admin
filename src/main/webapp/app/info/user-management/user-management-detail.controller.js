(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('UserManagementDetailController', UserManagementDetailController);

    UserManagementDetailController.$inject = ['$stateParams', 'User'];

    function UserManagementDetailController ($stateParams, User) {
        var vm = this;

        vm.load = load;
        vm.user = {};

        vm.load($stateParams.login);
        vm.genders = [{key:1,val:"男"},{key:2,val:"女"}];
        
        User.queryAllAuthorities({},function onSuccess(data, headers) {
        	vm.authorities = data;
    		if(data && data.length > 0){
    			if(vm.user.authorities && vm.user.authorities.length > 0){
    				var authorities = [];
    				for(var i = 0; i < vm.user.authorities.length; i++){
    					for(var j = 0; j < vm.authorities.length; j++){
    						if(vm.authorities[j].name == vm.user.authorities[i]){
    							authorities.push(vm.authorities[j].detail);
    							break;
    						}
    					}
    				}
    				vm.user.authorities = authorities;
    			}
    		}
    	});
        
        function load (login) {
            User.get({login: login}, function(result) {
                vm.user = result;
                for(var j = 0; j < vm.genders.length ; j++){
            		if(vm.user.gender == vm.genders[j].key){
        				vm.user.genderName = vm.genders[j].val;
        			}
            	}
            });
        }
    }
})();
