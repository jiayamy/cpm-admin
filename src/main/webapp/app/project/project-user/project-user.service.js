(function() {
    'use strict';
    angular
        .module('cpmApp')
        .factory('ProjectUser', ProjectUser);

    ProjectUser.$inject = ['$resource', 'DateUtils'];

    function ProjectUser ($resource, DateUtils) {
        var resourceUrl =  'api/project-users/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.joinDay = DateUtils.convertDateTimeFromServer(data.joinDay);
                        data.goodbyeDay = DateUtils.convertDateTimeFromServer(data.goodbyeDay);
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
