(function() {
    'use strict';
    angular
        .module('cpmApp')
        .factory('ProjectMonthlyStatChart', ProjectMonthlyStatChart);

    ProjectMonthlyStatChart.$inject = ['$resource', 'DateUtils'];

    function ProjectMonthlyStatChart ($resource, DateUtils) {
        var resourceUrl =  'api/project-monthly-stats/:id';

        return $resource(resourceUrl, {}, {
            'queryChart':{
            	url:'api/project-monthly-stats/queryChart',
            	method:'GET'
            }
        });
    }
})();
