(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ProjectInfoDetailController', ProjectInfoDetailController);

    ProjectInfoDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'previousState', 'entity', 'ProjectInfo'];

    function ProjectInfoDetailController($scope, $rootScope, $stateParams, previousState, entity, ProjectInfo) {
        var vm = this;

        vm.projectInfo = entity;
        vm.previousState = previousState.name;
        vm.loadProjectInfoUsers = loadProjectInfoUsers;//加载项目人员信息
        
        loadProjectInfoUsers();
        function loadProjectInfoUsers(){
        	ProjectInfo.queryProjectInfoUser({projectId:vm.projectInfo.id},
        			onSuccess,onError);
            
            function onSuccess(data){
            	vm.projectInfoUsers = data;
            }
            
            function onError(error){
            	 AlertService.error(error.data.message);
            }
        }
    }
})();
