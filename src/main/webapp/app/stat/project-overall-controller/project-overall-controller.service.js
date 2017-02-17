(function() {
    'use strict';
    angular
        .module('cpmApp')
        .factory('ProjectOverall', ProjectOverall);

    ProjectOverall.$inject = ['$resource', 'DateUtils'];

    function ProjectOverall ($resource, DateUtils) {
        var resourceUrl =  'api/project-overall-controller/:id';

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
            'queryDetail':{
            	url:'api/project-overall-controller/queryDetail',
            	method:'GET',
            	isArray:true
            }
        });
    }
})();
