(function() {
    'use strict';
    angular
        .module('cpmApp')
        .factory('SystemConfig', SystemConfig);

    SystemConfig.$inject = ['$resource'];

    function SystemConfig ($resource) {
        var resourceUrl =  'api/system-config/:id';

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
