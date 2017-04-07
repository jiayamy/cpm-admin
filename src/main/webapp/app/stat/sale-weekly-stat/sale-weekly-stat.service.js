(function() {
    'use strict';
    angular
        .module('cpmApp')
        .factory('SaleWeeklyStat', SaleWeeklyStat);

    SaleWeeklyStat.$inject = ['$resource', 'DateUtils'];

    function SaleWeeklyStat ($resource, DateUtils) {
        var resourceUrl =  'api/sale-weekly-stats/:id';

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
            'queryUserContract':{
            	url:'api/contract-weekly-stats/queryUserContract',
            	method:'GET',
            	isArray:true
            }
        });
    }
})();
