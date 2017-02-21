(function() {
    'use strict';
    angular
        .module('cpmApp')
        .factory('ConsultantBonus', ConsultantBonus);

    ConsultantBonus.$inject = ['$resource', 'DateUtils'];

    function ConsultantBonus ($resource, DateUtils) {
        var resourceUrl =  'api/consultant-bonus/:id';

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
            'update': { method:'PUT' },
            'queryConsultantRecord':{
            	url:'api/consultant-bonus/queryConsultantRecord',
            	method:'GET',
            	isArray:true
            }
        });
    }
})();
