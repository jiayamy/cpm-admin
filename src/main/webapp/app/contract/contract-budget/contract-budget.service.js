(function() {
    'use strict';
    angular
        .module('cpmApp')
        .factory('ContractBudget', ContractBudget);

    ContractBudget.$inject = ['$resource', 'DateUtils'];

    function ContractBudget ($resource, DateUtils) {
        var resourceUrl =  'api/contract-budgets/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.createTime = DateUtils.convertDateTimeFromServer(data.createTime);
                        data.updateTime = DateUtils.convertDateTimeFromServer(data.updateTime);
                        if (data.status == 1) {
							data.statusName = "可用";
						}else if (data.status == 2) {
							data.statusName = "删除"
						}
                        if (data.purchaseType == 1) {
							data.purchaseTypeName = "硬件";
						}else if (data.purchaseType == 2) {
							data.purchaseTypeName = "软件";
						}else if (data.purchaseType == 3) {
							data.purchaseTypeName = "服务";
						}
                    }
                    return data;
                }
            },
            'update': { method:'PUT' },
            'queryUserContract':{
            	url:'api/contract-budgets/queryUserContract',
            	method:'GET',
            	isArray:true
            },
            'queryUserContractBudget':{
            	url:'api/contract-budgets/queryUserContractBudget',
            	method:'GET',
            	isArray:true
            }
        });
    }
})();
