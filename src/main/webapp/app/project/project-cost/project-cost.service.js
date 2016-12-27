(function() {
    'use strict';
    angular
        .module('cpmApp')
        .factory('ProjectCost', ProjectCost);

    ProjectCost.$inject = ['$resource', 'DateUtils'];

    function ProjectCost ($resource, DateUtils) {
        var resourceUrl =  'api/project-costs/:id';

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
