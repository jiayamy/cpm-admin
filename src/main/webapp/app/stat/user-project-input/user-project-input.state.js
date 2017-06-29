(function(){
	'use strict';
	
	angular
		.module('cpmApp')
		.config(stateConfig);
	
	stateConfig.$inject = ['$stateProvider'];
	
	function stateConfig($stateProvider){
		$stateProvider
			.state('user-project-input',{
				parent:'stat',
				url:'/user-project-input?startTime&endTime&userId&userName&projectId&showTotal',
				data:{
				      authorities:['ROLE_STAT_USER_PROJECT_INPUT'],
				      pageTitel:'cpmApp.userProjectInput.home.title'
				},
				views:{
					'content@': {
	                    templateUrl: 'app/stat/user-project-input/user-project-input.html',
	                    controller: 'UserProjectInputController',
	                    controllerAs: 'vm'
	                }
				},
				params:{
					startTime: null,
					endTime: null,
					userId : null,
					userName : null,
					projectId : null,
//					projectName : null,
					showTotal : null
				},
				resolve:{
	            	loadMyCtrl:['$ocLazyLoad',function($ocLazyLoad){
	            		return $ocLazyLoad.load([
	                                             'app/stat/user-project-input/user-project-input.service.js',
	                                             'app/stat/user-project-input/user-project-input.controller.js',
	                                             'app/project/project-info/project-info.service.js']);
	            	}],
	            	pagingParams:['$stateParams','PaginationUtil',function($stateParams,PaginationUtil){
	            		return {
	                        startTime: $stateParams.startTime,
	                        endTime: $stateParams.endTime,
	                        userId: $stateParams.userId,
	                        userName: $stateParams.userName,
	                        projectId: $stateParams.projectId,
	                        projectName: $stateParams.projectName,
	                        showTotal: $stateParams.showTotal
	            		}
	            	}],
	            	translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
	                    $translatePartialLoader.addPart('userProjectInput');
	                    $translatePartialLoader.addPart('global');
	                    $translatePartialLoader.addPart('deptInfo');
	                    return $translate.refresh();
	                }]
				}
			})
			.state('user-project-input.queryDept',{
				parent: 'user-project-input',
				url: '/queryDept?selectType&showChild',
				data: {
					authorities: ['ROLE_STAT_USER_PROJECT_INPUT']
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