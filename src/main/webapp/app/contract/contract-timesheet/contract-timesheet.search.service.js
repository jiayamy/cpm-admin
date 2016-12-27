(function() {
    'use strict';

    angular
        .module('cpmApp')
        .factory('ContractTimesheetSearch', ContractTimesheetSearch);

    ContractTimesheetSearch.$inject = ['$resource'];

    function ContractTimesheetSearch($resource) {
        var resourceUrl =  'api/_search/contract-timesheets/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
