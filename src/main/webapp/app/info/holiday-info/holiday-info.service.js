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
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
