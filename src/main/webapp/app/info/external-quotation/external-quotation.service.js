(function() {
    'use strict';
    angular
        .module('cpmApp')
        .factory('ExternalQuotation', ExternalQuotation);

    ExternalQuotation.$inject = ['$resource'];

    function ExternalQuotation ($resource) {
        var resourceUrl =  'api/external-quotation/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
