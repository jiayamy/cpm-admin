(function() {
    'use strict';

    angular
        .module('cpmApp')
        .factory('ProjectUserSearch', ProjectUserSearch);

    ProjectUserSearch.$inject = ['$resource'];

    function ProjectUserSearch($resource) {
        var resourceUrl =  'api/_search/project-users/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
