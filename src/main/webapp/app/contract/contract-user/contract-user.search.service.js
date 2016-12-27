(function() {
    'use strict';

    angular
        .module('cpmApp')
        .factory('ContractUserSearch', ContractUserSearch);

    ContractUserSearch.$inject = ['$resource'];

    function ContractUserSearch($resource) {
        var resourceUrl =  'api/_search/contract-users/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
