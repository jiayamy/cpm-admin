(function() {
    'use strict';
    angular
        .module('cpmApp')
        .factory('BonusRate', BonusRate);

    BonusRate.$inject = ['$resource'];

    function BonusRate ($resource) {
        var resourceUrl =  'api/bonus-rate/:id';

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
