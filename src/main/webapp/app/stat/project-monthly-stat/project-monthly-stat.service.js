(function() {
    'use strict';
    angular
        .module('cpmApp')
        .factory('ProjectMonthlyStat', ProjectMonthlyStat);

    ProjectMonthlyStat.$inject = ['$resource', 'DateUtils'];

    function ProjectMonthlyStat ($resource, DateUtils) {
        var resourceUrl =  'api/project-monthly-stats/:id';

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
