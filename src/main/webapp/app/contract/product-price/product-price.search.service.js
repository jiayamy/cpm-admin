(function() {
    'use strict';

    angular
        .module('cpmApp')
        .factory('ProductPriceSearch', ProductPriceSearch);

    ProductPriceSearch.$inject = ['$resource'];

    function ProductPriceSearch($resource) {
        var resourceUrl =  'api/_search/product-prices/:id';

        return $resource(resourceUrl, {}, {
        	'query': {
                method: 'GET',
                isArray: true,
                params: {name: null, type: null, source: null}
            }
        });
    }
})();
