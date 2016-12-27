(function() {
    'use strict';

    angular
        .module('cpmApp')
        .factory('ProjectFinishInfoSearch', ProjectFinishInfoSearch);

    ProjectFinishInfoSearch.$inject = ['$resource'];

    function ProjectFinishInfoSearch($resource) {
        var resourceUrl =  'api/_search/project-finish-infos/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
