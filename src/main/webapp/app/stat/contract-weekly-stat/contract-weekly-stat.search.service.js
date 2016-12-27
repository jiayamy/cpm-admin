(function() {
    'use strict';

    angular
        .module('cpmApp')
        .factory('ContractWeeklyStatSearch', ContractWeeklyStatSearch);

    ContractWeeklyStatSearch.$inject = ['$resource'];

    function ContractWeeklyStatSearch($resource) {
        var resourceUrl =  'api/_search/contract-weekly-stats/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
