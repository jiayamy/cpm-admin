(function() {
    'use strict';
    angular
        .module('cpmApp')
        .factory('HolidayInfo', HolidayInfo);

    HolidayInfo.$inject = ['$resource', 'DateUtils'];

    function HolidayInfo ($resource, DateUtils) {
        var resourceUrl =  'api/holiday-infos/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.createTime = DateUtils.convertDateTimeFromServer(data.createTime);
                        data.updateTime = DateUtils.convertDateTimeFromServer(data.updateTime);
                        if(data.type == 1){
                        	data.typeName = "正常工作日";
                        }else if(data.type == 2){
                        	data.typeName = "正常休息日";
                        }else if(data.type == 3){
                        	data.typeName = "年假";
                        }if(data.type == 4){
                        	data.typeName = "国家假日";
                        }
                    }
                    return data;
                }
            },
            'update': { method:'PUT' },
            'updateByOne': { method:'PUT',url:"api/holiday-infos/updateByOne" },
            'queryCalendar': {method:'GET',url:"api/holiday-infos/queryCalendar"}
        });
    }
})();
