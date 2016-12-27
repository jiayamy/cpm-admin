(function() {
    'use strict';

    angular
        .module('cpmApp')
        .factory('ContractBudgetSearch', ContractBudgetSearch);

    ContractBudgetSearch.$inject = ['$resource'];

    function ContractBudgetSearch($resource) {
        var resourceUrl =  'api/_search/contract-budgets/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
