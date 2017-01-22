(function() {
    'use strict';
    angular
        .module('cpmApp')
        .factory('ContractCost', ContractCost);

    ContractCost.$inject = ['$resource', 'DateUtils'];

    function ContractCost ($resource, DateUtils) {
        var resourceUrl =  'api/contract-costs/:id';

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
            'update': { method:'PUT' },
            'queryContractBudgets':{ 
            	url:'api/contract-costs/queryBudges',
            	method: 'GET', 
            	isArray: true
            },
            'queryAllBudges':{ 
            	url:'api/contract-costs/queryAllBudges',
            	method: 'GET', 
            	isArray: true
            }
        });
    }
})();
