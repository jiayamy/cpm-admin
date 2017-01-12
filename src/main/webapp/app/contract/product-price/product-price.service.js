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
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.createTime = DateUtils.convertDateTimeFromServer(data.createTime);
                        data.updateTime = DateUtils.convertDateTimeFromServer(data.updateTime);
                        if(data.type == 0){
            				data.typeName = "硬件";
            			}else if(data.type == 1){
            				data.typeName = "软件";
            			}
            			if(data.source == 0){
            				data.sourceName = "外部";
            			}else if(data.source == 1){
            				data.sourceName = "内部";
            			}
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
