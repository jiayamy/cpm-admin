(function() {
    'use strict';

    angular
        .module('cpmApp')
        .factory('DeptInfoSearch', DeptInfoSearch);

    DeptInfoSearch.$inject = ['$resource'];

    function DeptInfoSearch($resource) {
        var resourceUrl =  'api/_search/dept-infos/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
