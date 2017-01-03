(function() {
    'use strict';

    angular
        .module('cpmApp')
        .factory('UserTimesheetSearch', UserTimesheetSearch);

    UserTimesheetSearch.$inject = ['$resource'];

    function UserTimesheetSearch($resource) {
        var resourceUrl =  'api/_edit/user-timesheets/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'update': { method:'PUT' }
        });
    }
})();
