(function() {
    'use strict';

    angular
        .module('cpmApp')
        .factory('DeptTypeSearch', DeptTypeSearch);

    DeptTypeSearch.$inject = ['$resource'];

    function DeptTypeSearch($resource) {
        var resourceUrl =  'api/_search/dept-types/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
