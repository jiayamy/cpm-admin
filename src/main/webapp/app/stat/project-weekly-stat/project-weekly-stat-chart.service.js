(function() {
    'use strict';
    angular
        .module('cpmApp')
        .factory('ProjectWeeklyStatChart', ProjectWeeklyStatChart);

    ProjectWeeklyStatChart.$inject = ['$resource', 'DateUtils'];

    function ProjectWeeklyStatChart ($resource, DateUtils) {
        var resourceUrl =  'api/project-weekly-stats/:id';

        return $resource(resourceUrl, {}, {
            'queryChart':{
            	url:'api/project-weekly-stats/queryChart',
            	method:'GET'
            }
        });
    }
})();
