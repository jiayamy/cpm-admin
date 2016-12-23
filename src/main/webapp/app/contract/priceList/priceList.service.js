(function() {
    'use strict';

    angular
        .module('cpmApp')
        .factory('PriceListService', PriceListService);

    PriceListService.$inject = ['$resource'];

    function PriceListService ($resource) {
        var service = $resource('contract/priceList', {}, {
            'query': {
                method: 'GET',
                isArray: true,
                params: {name: null, type: null, source: null}
            }
        });

        return service;
    }
})();
