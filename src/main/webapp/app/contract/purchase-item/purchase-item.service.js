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
                        
                        if (data.status == 1) {
							data.statusName = "可用";
						}else if (data.status == 2) {
							data.statusName = "已删除";
						}
                        if (data.source == 1) {
							data.sourceName = "内部";
						}else if (data.source == 2) {
							data.sourceName = "外部";
						}
                        if (data.type == 1) {
							data.typeName = "硬件";
						}else if (data.type == 2) {
							data.typeName = "软件";
						}
                    }
                    return data;
                }
            },
            'update': { method:'PUT' },
            'queryUserContract':{
            	url:'api/purchase-item/queryUserContract',
            	method:'GET',
            	isArray:true
            },
            'queryProductPrice':{
            	url:'api/purchase-item/queryProductPrice',
            	method:'GET',
            	isArray:true
            }
        });
    }
})();
