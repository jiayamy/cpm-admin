(function() {
    'use strict';
    angular
        .module('cpmApp')
        .factory('SalePurchaseInternalCost', SalePurchaseInternalCost);

    SalePurchaseInternalCost.$inject = ['$resource', 'DateUtils'];

    function SalePurchaseInternalCost ($resource, DateUtils) {
        var resourceUrl =  'api/sale-purchase-internalCost/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.createTime = DateUtils.convertDateTimeFromServer(data.createTime);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' },
            'queryDetail':{
            	url:'api/sale-purchase-internalCost/queryInternalCostDetail',
            	method:'GET',
            	isArray:true
            }
        });
    }
})();
