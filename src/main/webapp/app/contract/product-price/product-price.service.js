(function() {
    'use strict';
    angular
        .module('cpmApp')
        .factory('ProductPrice', ProductPrice);

    ProductPrice.$inject = ['$resource', 'DateUtils'];

    function ProductPrice ($resource, DateUtils) {
        var resourceUrl =  'api/product-prices/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'find': {
                method: 'GET',
                isArray: true,
                params: {name: null, type: null, source: null}
            },
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.createTime = DateUtils.convertDateTimeFromServer(data.createTime);
                        data.updateTime = DateUtils.convertDateTimeFromServer(data.updateTime);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
