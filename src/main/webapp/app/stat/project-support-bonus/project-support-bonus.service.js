(function() {
    'use strict';
    angular
        .module('cpmApp')
        .factory('ProjectSupportBonus', ProjectSupportBonus);

    ProjectSupportBonus.$inject = ['$resource', 'DateUtils'];

    function ProjectSupportBonus ($resource, DateUtils) {
        var resourceUrl =  'api/project-support-bonus/:id';

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
            	url:'api/project-support-bonus/queryDetail',
            	method:'GET',
            	isArray:true
            }
        });
    }
})();
