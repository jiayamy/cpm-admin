(function() {
    'use strict';

    angular
        .module('cpmApp')
        .factory('HolidayInfoSearch', HolidayInfoSearch);

    HolidayInfoSearch.$inject = ['$resource'];

    function HolidayInfoSearch($resource) {
        var resourceUrl =  'api/_search/holiday-infos/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
