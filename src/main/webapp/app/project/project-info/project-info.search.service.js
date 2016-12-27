(function() {
    'use strict';

    angular
        .module('cpmApp')
        .factory('ProjectInfoSearch', ProjectInfoSearch);

    ProjectInfoSearch.$inject = ['$resource'];

    function ProjectInfoSearch($resource) {
        var resourceUrl =  'api/_search/project-infos/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
