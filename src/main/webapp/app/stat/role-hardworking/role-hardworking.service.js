(function() {
    'use strict';
    angular
        .module('cpmApp')
        .factory('RoleHardWorking', RoleHardWorking);

    RoleHardWorking.$inject = ['$resource', 'DateUtils'];

    function RoleHardWorking ($resource, DateUtils) {
        var resourceUrl =  'api/role-hardworking/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
