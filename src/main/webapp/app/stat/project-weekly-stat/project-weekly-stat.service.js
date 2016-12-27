(function() {
    'use strict';
    angular
        .module('cpmApp')
        .factory('ProjectWeeklyStat', ProjectWeeklyStat);

    ProjectWeeklyStat.$inject = ['$resource', 'DateUtils'];

    function ProjectWeeklyStat ($resource, DateUtils) {
        var resourceUrl =  'api/project-weekly-stats/:id';

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
