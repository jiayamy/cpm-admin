(function() {
    'use strict';
    angular
        .module('cpmApp')
        .factory('PurchaseItem', PurchaseItem);

    PurchaseItem.$inject = ['$resource', 'DateUtils'];

    function PurchaseItem ($resource, DateUtils) {
        var resourceUrl =  'api/purchase-items/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
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
