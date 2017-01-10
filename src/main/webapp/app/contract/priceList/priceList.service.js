(function() {
    'use strict';

    angular
        .module('cpmApp')
        .factory('PriceListService', PriceListService);

    PriceListService.$inject = ['$resource'];

    function PriceListService ($resource) {
        var service = $resource('contract/priceList/:id', {}, {
            'query': {
                method: 'GET',
                isArray: true,
                params: {name: null, type: null, source: null}
            },
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    data = angular.fromJson(data);
                    return data;
                }
            },
            'save': { method:'POST' },
            'update': { method:'PUT' },
            'delete':{ method:'DELETE'}
        });
        return service;
    }
})();
