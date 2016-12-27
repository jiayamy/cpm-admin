(function() {
    'use strict';

    angular
        .module('cpmApp')
        .factory('UserCostSearch', UserCostSearch);

    UserCostSearch.$inject = ['$resource'];

    function UserCostSearch($resource) {
        var resourceUrl =  'api/_search/user-costs/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
