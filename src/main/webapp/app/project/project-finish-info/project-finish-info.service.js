(function() {
    'use strict';
    angular
        .module('cpmApp')
        .factory('ProjectFinishInfo', ProjectFinishInfo);

    ProjectFinishInfo.$inject = ['$resource', 'DateUtils'];

    function ProjectFinishInfo ($resource, DateUtils) {
        var resourceUrl =  'api/project-finish-infos/:id';

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
