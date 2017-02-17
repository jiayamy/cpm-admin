(function() {
    'use strict';
    angular
        .module('cpmApp')
        .factory('SalesBonus', SalesBonus);

    SalesBonus.$inject = ['$resource', 'DateUtils'];

    function SalesBonus ($resource, DateUtils) {
        var resourceUrl =  'api/sales-bonus/:id';

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
            'queryContractInfo':{
            	url:'api/contract-infos/queryUserContract',
            	method:'GET',
            	isArray:true
            }
        });
    }
})();
