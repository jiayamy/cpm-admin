(function() {
    'use strict';
    angular
        .module('cpmApp')
        .factory('ProjectInfo', ProjectInfo);

    ProjectInfo.$inject = ['$resource', 'DateUtils'];

    function ProjectInfo ($resource, DateUtils) {
        var resourceUrl =  'api/project-infos/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.startDay = DateUtils.convertDateTimeFromServer(data.startDay);
                        data.endDay = DateUtils.convertDateTimeFromServer(data.endDay);
                        data.createTime = DateUtils.convertDateTimeFromServer(data.createTime);
                        data.updateTime = DateUtils.convertDateTimeFromServer(data.updateTime);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' },
            'queryUserContract':{
            	url:'api/project-infos/queryUserContract',
            	method:'GET',
            	isArray:true
            }
        });
    }
})();
