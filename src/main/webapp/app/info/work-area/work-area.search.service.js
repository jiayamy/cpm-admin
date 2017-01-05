(function() {
    'use strict';

    angular
        .module('cpmApp')
        .factory('WorkAreaSearch', WorkAreaSearch);

    WorkAreaSearch.$inject = ['$resource'];

    function WorkAreaSearch($resource) {
        var resourceUrl =  'api/_search/work-areas/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
