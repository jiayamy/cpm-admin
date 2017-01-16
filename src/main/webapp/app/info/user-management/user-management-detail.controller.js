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
