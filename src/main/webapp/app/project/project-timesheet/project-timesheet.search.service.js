(function() {
    'use strict';

    angular
        .module('cpmApp')
        .factory('ProjectTimesheetSearch', ProjectTimesheetSearch);

    ProjectTimesheetSearch.$inject = ['$resource'];

    function ProjectTimesheetSearch($resource) {
        var resourceUrl =  'api/_search/project-timesheets/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
