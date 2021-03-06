(function() {
    'use strict';
    angular
        .module('cpmApp')
        .factory('Bonus', Bonus);

    Bonus.$inject = ['$resource', 'DateUtils'];

    function Bonus ($resource, DateUtils) {
        var resourceUrl =  'api/bonus/:id';

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
            	url:'api/bonus/queryDetail',
            	method:'GET',
            	isArray:true
            }
        });
    }
})();
