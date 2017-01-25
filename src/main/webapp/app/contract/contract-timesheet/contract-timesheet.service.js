(function() {
    'use strict';
    angular
        .module('cpmApp')
        .factory('ContractTimesheet', ContractTimesheet);

    ContractTimesheet.$inject = ['$resource', 'DateUtils'];

    function ContractTimesheet ($resource, DateUtils) {
        var resourceUrl =  'api/contract-timesheets/:id';

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
            'update': { method:'PUT' },
            'getEditUserTimesheets':{
            	url:'api/contract-timesheets/queryEdit',
            	method: 'GET', isArray: true
            }
        });
    }
})();
