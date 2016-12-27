(function() {
    'use strict';
    angular
        .module('cpmApp')
        .factory('UserCost', UserCost);

    UserCost.$inject = ['$resource', 'DateUtils'];

    function UserCost ($resource, DateUtils) {
        var resourceUrl =  'api/user-costs/:id';

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
