(function() {
    'use strict';

    angular
        .module('cpmApp')
        .factory('ProjectCostSearch', ProjectCostSearch);

    ProjectCostSearch.$inject = ['$resource'];

    function ProjectCostSearch($resource) {
        var resourceUrl =  'api/_search/project-costs/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
