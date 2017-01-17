(function() {
    'use strict';
    angular
        .module('cpmApp')
        .factory('ContractInfo', ContractInfo);

    ContractInfo.$inject = ['$resource', 'DateUtils'];

    function ContractInfo ($resource, DateUtils,$http) {
    	
        var resourceUrl =  'api/contract-infos/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.startDay = DateUtils.convertDateTimeFromServer(data.startDay);
                        data.endDay = DateUtils.convertDateTimeFromServer(data.endDay);
                        data.createTime = DateUtils.convertDateTimeFromServer(data.createTime);
                        data.updateTime = DateUtils.convertDateTimeFromServer(data.updateTime);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' },
            'queryContractInfo':{
            	url:'api/contract-infos/queryUserContract',
            	method:'GET',
            	isArray:true
            },
            'finish': { method:'PUT',url:'api/contract-infos/finish' }
        });
    }
})();
