(function() {
    'use strict';
    angular
        .module('cpmApp')
        .factory('WorkArea', WorkArea);

    WorkArea.$inject = ['$resource'];

    function WorkArea ($resource) {
        var resourceUrl =  'api/work-areas/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' },
            'queryAll':{
            	method:'GET',
            	url:'api/work-areas/queryAll',
            	isArray:true
            }
        });
    }
})();
