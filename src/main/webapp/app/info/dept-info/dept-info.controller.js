(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('DeptInfoController', DeptInfoController);

    DeptInfoController.$inject = ['$scope','$rootScope', '$state', 'DeptInfo', 'ParseLinks', 'AlertService', 'paginationConstants', 'pagingParams'];

    function DeptInfoController ($scope,$rootScope, $state, DeptInfo, ParseLinks, AlertService, paginationConstants, pagingParams) {
        var vm = this;

        vm.isChildShowed = isChildShowed;
        vm.showOrHiddenChild = showOrHiddenChild;

        loadAll();
        function loadAll () {
            DeptInfo.getDeptTree({
            	
            }, onSuccess, onError);
            
            function onSuccess(data, headers) {
            	vm.deptInfos = data;
            	for(var i = 0; i < vm.deptInfos.length; i++){
                    if(vm.deptInfos[i].children && vm.deptInfos[i].children.length !=0){
                    	vm.deptInfos[i].showChild = true;
                    }
                }
            }
            function onError(error) {
                AlertService.error(error.data.message);
            }
        }
        
        function isChildShowed(node){
        	if(node.children && node.children.length != 0 && node.showChild){
                return true;
            }
            return false;
        }
        
        function showOrHiddenChild(node){
        	if(node.children && node.children.length != 0){
                node.showChild = !node.showChild;
            }
        	return;
        }
        
        var unsubscribe = $rootScope.$on('cpmApp:deptInfoUpdate', function(event, result) {
//        	loadAll();
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
