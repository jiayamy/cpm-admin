(function() {
    'use strict';
    angular
        .module('cpmApp')
        .factory('ContractUser', ContractUser);

    ContractUser.$inject = ['$resource', 'DateUtils'];

    function ContractUser ($resource, DateUtils) {
        var resourceUrl =  'api/contract-users/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.joinDay = DateUtils.convertDateTimeFromServer(data.joinDay);
                        data.leaveDay = DateUtils.convertDateTimeFromServer(data.leaveDay);
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
