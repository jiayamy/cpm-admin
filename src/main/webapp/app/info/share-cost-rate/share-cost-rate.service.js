(function() {
    'use strict';
    angular
        .module('cpmApp')
        .factory('ShareCostRate', ShareCostRate);

    ShareCostRate.$inject = ['$resource'];

    function ShareCostRate ($resource) {
        var resourceUrl =  'api/share-cost-rate/:id';

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
