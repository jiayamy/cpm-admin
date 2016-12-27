(function() {
    'use strict';

    angular
        .module('cpmApp')
        .factory('ContractReceiveSearch', ContractReceiveSearch);

    ContractReceiveSearch.$inject = ['$resource'];

    function ContractReceiveSearch($resource) {
        var resourceUrl =  'api/_search/contract-receives/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
