(function() {
    'use strict';

    angular
        .module('cpmApp')
        .factory('ProjectWeeklyStatSearch', ProjectWeeklyStatSearch);

    ProjectWeeklyStatSearch.$inject = ['$resource'];

    function ProjectWeeklyStatSearch($resource) {
        var resourceUrl =  'api/_search/project-weekly-stats/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
