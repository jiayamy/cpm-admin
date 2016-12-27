(function() {
    'use strict';

    angular
        .module('cpmApp')
        .factory('UserTimesheetSearch', UserTimesheetSearch);

    UserTimesheetSearch.$inject = ['$resource'];

    function UserTimesheetSearch($resource) {
        var resourceUrl =  'api/_search/user-timesheets/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
