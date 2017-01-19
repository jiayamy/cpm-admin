(function() {
    'use strict';
    angular
        .module('cpmApp')
        .factory('ContractMonthlyStatChart', ContractMonthlyStatChart);

    ContractMonthlyStatChart.$inject = ['$resource', 'DateUtils'];

    function ContractMonthlyStatChart ($resource, DateUtils) {
        var resourceUrl =  'api/contract-monthly-stats/:id';

        return $resource(resourceUrl, {}, {
            'queryChart':{
            	url:'api/contract-monthly-stats/queryChart',
            	method:'GET'
            },
            'queryFinishRateChart':{
            	url:'api/contract-monthly-stats/queryFinishRateChart',
            	method:'GET'
            }
        });
    }
})();
