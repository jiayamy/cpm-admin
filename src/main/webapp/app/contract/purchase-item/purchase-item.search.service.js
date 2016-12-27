(function() {
    'use strict';

    angular
        .module('cpmApp')
        .factory('PurchaseItemSearch', PurchaseItemSearch);

    PurchaseItemSearch.$inject = ['$resource'];

    function PurchaseItemSearch($resource) {
        var resourceUrl =  'api/_search/purchase-items/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
