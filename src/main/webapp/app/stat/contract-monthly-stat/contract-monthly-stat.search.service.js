(function() {
    'use strict';

    angular
        .module('cpmApp')
        .factory('ContractMonthlyStatSearch', ContractMonthlyStatSearch);

    ContractMonthlyStatSearch.$inject = ['$resource'];

    function ContractMonthlyStatSearch($resource) {
        var resourceUrl =  'api/_search/contract-monthly-stats/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
