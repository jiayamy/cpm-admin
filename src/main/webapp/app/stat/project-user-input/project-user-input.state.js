(function(){
	'use strict';
	
	angular
		.module('cpmApp')
		.config(stateConfig);
	
	stateConfig.$inject = ['$stateProvider'];
	
	function stateConfig($stateProvider){
		$stateProvider
		.state('project-user-input',{
			parent:'stat',
			url:'/project-user-input?startTime&endTime&userId&userName&showTotal&projectId',
			data:{
				authorities:['ROLE_STAT_PROJECT_USER_INPUT'],
				pageTitel:'cpmApp.projectUserInput.home.title'
			},
			views:{
				'content@': {
                    templateUrl: 'app/stat/project-user-input/project-user-input.html',
                    controller: 'ProjectUserInputController',
                    controllerAs: 'vm'
                }
			},
			params: {
                startTime: null,
                endTime: null,
                userId: null,
                userName:null,
                projectId : null,
                showTotal:null
            },
            resolve:{
            	loadMyCtrl:['$ocLazyLoad',function($ocLazyLoad){
            		return $ocLazyLoad.load([
                                             'app/stat/project-user-input/project-user-input.service.js',
                                             'app/stat/project-user-input/project-user-input.controller.js',
                                             'app/project/project-info/project-info.service.js']);
            	}],
            	pagingParams:['$stateParams','PaginationUtil',function($stateParams,PaginationUtil){
            		return {
                        startTime: $stateParams.startTime,
                        endTime: $stateParams.endTime,
                        userId: $stateParams.userId,
                        userName: $stateParams.userName,
                        projectId: $stateParams.projectId,
                        showTotal: $stateParams.showTotal
            		}
            	}],
            	translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('projectUserInput');
                    $translatePartialLoader.addPart('global');
                    $translatePartialLoader.addPart('deptInfo');
                    return $translate.refresh();
                }]
            }
		})
		.state('project-user-input.queryDept',{
            parent: 'project-user-input',
            url: '/queryDept?selectType&showChild',
            data: {
                authorities: ['ROLE_STAT_PROJECT_USER_INPUT']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/info/dept-info/dept-info-queryMultiSelect.html',
                    controller: 'DeptInfoQueryMultiSelectController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                    	loadMyCtrl: ['$ocLazyLoad', function($ocLazyLoad){
                            return $ocLazyLoad.load([
                                                     'app/info/dept-info/dept-info-queryMultiSelect.controller.js',
                                                     'app/info/dept-info/dept-info.service.js'
                                                     ]);
                        }],
                        entity: function() {
                            return {
                            	selectType : $stateParams.selectType,
                            	showChild : $stateParams.showChild
                            }
                        }
                    }
                }).result.then(function() {
                    $state.go('^', {}, { reload: false });
                }, function() {
                    $state.go('^');
                });
            }]
		});
	}
})();