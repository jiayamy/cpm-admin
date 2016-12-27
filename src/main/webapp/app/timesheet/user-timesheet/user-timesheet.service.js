(function() {
    'use strict';
    angular
        .module('cpmApp')
        .factory('UserTimesheet', UserTimesheet);

    UserTimesheet.$inject = ['$resource', 'DateUtils'];

    function UserTimesheet ($resource, DateUtils) {
        var resourceUrl =  'api/user-timesheets/:id';

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
