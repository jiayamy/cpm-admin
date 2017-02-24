(function() {
    'use strict';
    angular
        .module('cpmApp')
        .factory('SalesAnnualIndex', SalesAnnualIndex);

    SalesAnnualIndex.$inject = ['$resource'];

    function SalesAnnualIndex ($resource) {
        var resourceUrl =  'api/sales-annualIndex/:id';

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
