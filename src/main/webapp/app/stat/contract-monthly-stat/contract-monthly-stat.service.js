(function() {
    'use strict';
    angular
        .module('cpmApp')
        .factory('ContractMonthlyStat', ContractMonthlyStat);

    ContractMonthlyStat.$inject = ['$resource', 'DateUtils'];

    function ContractMonthlyStat ($resource, DateUtils) {
        var resourceUrl =  'api/contract-monthly-stats/:id';

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
            'update': { method:'PUT' }
        });
    }
})();
