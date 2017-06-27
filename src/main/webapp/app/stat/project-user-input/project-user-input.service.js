(function(){
	'use strict';
	
	angular
		.module('cpmApp')
		.factory('ProjectUserInput',ProjectUserInput);
	
	ProjectUserInput.$inject = ['$resource', 'DateUtils'];
	
	function ProjectUserInput($resource,DateUtils){
		var resourceUrl =  'api/project-user-input/:id';
		
		return $resource(resourceUrl,{},{
			'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',isArray: true
            },
            'update': { method:'PUT' }
		});
	}
})();