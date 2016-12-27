(function() {
    'use strict';
    angular
        .module('cpmApp')
        .factory('ProjectTimesheet', ProjectTimesheet);

    ProjectTimesheet.$inject = ['$resource', 'DateUtils'];

    function ProjectTimesheet ($resource, DateUtils) {
        var resourceUrl =  'api/project-timesheets/:id';

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
