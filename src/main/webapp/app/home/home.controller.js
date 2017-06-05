(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('HomeController', HomeController);

    HomeController.$inject = ['$scope', 'Principal', 'LoginService', '$state','$rootScope', '$timeout', 'Auth'];

    function HomeController ($scope, Principal, LoginService, $state,$rootScope, $timeout, Auth) {
        var vm = this;
        vm.authenticationError = false;
        vm.rememberMe = true;
        
        vm.account = null;
        vm.isAuthenticated = null;
//        vm.login = LoginService.open;
        vm.login = login;
        vm.register = register;
        $scope.$on('authenticationSuccess', function() {
            getAccount();
        });

        getAccount();

        function getAccount() {
            Principal.identity().then(function(account) {
                vm.account = account;
                vm.isAuthenticated = Principal.isAuthenticated;
            });
        }
        function login (event) {
            event.preventDefault();
            Auth.login({
                username: vm.username,
                password: vm.password,
                rememberMe: vm.rememberMe
            }).then(function () {
                vm.authenticationError = false;
                if ($state.current.name === 'register' || $state.current.name === 'activate' ||
                    $state.current.name === 'finishReset' || $state.current.name === 'requestReset') {
                    $state.go('home');
                }
                $rootScope.$broadcast('authenticationSuccess');

                if (Auth.getPreviousState()) {
                    var previousState = Auth.getPreviousState();
                    Auth.resetPreviousState();
                    $state.go(previousState.name, previousState.params);
                }
            }).catch(function () {
                vm.authenticationError = true;
                $timeout(function () {
                	vm.authenticationError = false;
                }, 500);
            });
        }
        function register () {
            $state.go('register');
        }
    }
})();
