(function() {
    'use strict';

    angular
        .module('cpmApp')
        .factory('ContractInfoSearch', ContractInfoSearch);

    ContractInfoSearch.$inject = ['$resource'];

    function ContractInfoSearch($resource) {
        var resourceUrl =  'api/_search/contract-infos/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
