(function() {
    'use strict';
    angular
        .module('cpmApp')
        .factory('ContractWeeklyStatChart', ContractWeeklyStatChart);

    ContractWeeklyStatChart.$inject = ['$resource', 'DateUtils'];

    function ContractWeeklyStatChart ($resource, DateUtils) {
        var resourceUrl =  'api/contract-weekly-stats/:id';

        return $resource(resourceUrl, {}, {
            'queryChart':{
            	url:'api/contract-weekly-stats/queryChart',
            	method:'GET'
            },
            'queryFinishRateChart':{
            	url:'api/contract-weekly-stats/queryFinishRateChart',
            	method:'GET'
            }
        });
    }
})();
