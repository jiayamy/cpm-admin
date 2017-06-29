(function(){
	'use strict';
	
	angular
		.module('cpmApp')
		.factory('UserProjectInput',UserProjectInput);
	
	UserProjectInput.$inject = ['$resource', 'DateUtils'];
	
	function UserProjectInput($resource, DateUtils){
		var resourceUrl =  'api/user-project-input/:id';

		return $resource(resourceUrl,{},{
			'query': { method: 'GET', isArray: true},
			'get': {
				method: 'GET',isArray: true
			},
			'update': { method:'PUT' }
		});
	}
})();