(function() {
    'use strict';
    angular
        .module('cpmApp')
        .factory('RoleHardWorkingChart', RoleHardWorkingChart);

    RoleHardWorkingChart.$inject = ['$resource', 'DateUtils'];

    function RoleHardWorkingChart ($resource, DateUtils) {
    	var resourceUrl =  'api/role-hardworking/:id';

        return $resource(resourceUrl, {}, {
            'queryChart':{
            	url:'api/role-hardworking/queryChart',
            	method:'GET'
            }
        });
    }
})();
