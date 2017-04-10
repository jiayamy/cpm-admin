(function() {
    'use strict';
    angular
        .module('cpmApp')
        .factory('OutsourcingUser', OutsourcingUser);

    OutsourcingUser.$inject = ['$resource', 'DateUtils'];

    function OutsourcingUser ($resource, DateUtils,$http) {
    	
        var resourceUrl =  'api/outsourcing-user/:infoId';

        return $resource(resourceUrl, {}, {
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
            'getUserList':{
            	url:'api/outsourcing-user/getUserList',
            	method:'GET',
            	isArray:true
            },
            'queryRank':{
            	url:'api/outsourcing-user/queryUserRank',
            	method:'GET',
            	isArray:true
            }
        });
    }
})();
