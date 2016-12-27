(function() {
    'use strict';

    angular
        .module('cpmApp')
        .factory('ContractCostSearch', ContractCostSearch);

    ContractCostSearch.$inject = ['$resource'];

    function ContractCostSearch($resource) {
        var resourceUrl =  'api/_search/contract-costs/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
