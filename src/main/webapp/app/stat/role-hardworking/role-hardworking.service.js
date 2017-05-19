(function() {
    'use strict';
    angular
        .module('cpmApp')
        .factory('RoleHardWorking', RoleHardWorking);

    RoleHardWorking.$inject = ['$resource', 'DateUtils'];

    function RoleHardWorking ($resource, DateUtils) {
        var resourceUrl =  'api/role-hardworking/:id';

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
            }
        });
    }
})();
