(function() {
    'use strict';
    angular
        .module('cpmApp')
        .factory('DeptInfo', DeptInfo);

    DeptInfo.$inject = ['$resource', 'DateUtils'];

    function DeptInfo ($resource, DateUtils) {
        var resourceUrl =  'api/dept-infos/:id';

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
            'update': { method:'PUT' },
            'getDeptAndUserTree':{
            	method: 'GET',
            	url:'api/dept-infos/getDeptAndUserTree',
            	isArray: true
            }
            	
        });
    }
})();
