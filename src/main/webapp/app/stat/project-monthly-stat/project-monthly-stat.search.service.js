(function() {
    'use strict';

    angular
        .module('cpmApp')
        .factory('ProjectMonthlyStatSearch', ProjectMonthlyStatSearch);

    ProjectMonthlyStatSearch.$inject = ['$resource'];

    function ProjectMonthlyStatSearch($resource) {
        var resourceUrl =  'api/_search/project-monthly-stats/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
