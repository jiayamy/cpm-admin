(function() {
    'use strict';
    angular
        .module('cpmApp')
        .factory('DeptType', DeptType);

    DeptType.$inject = ['$resource'];

    function DeptType ($resource) {
        var resourceUrl =  'api/dept-types/:id';

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
