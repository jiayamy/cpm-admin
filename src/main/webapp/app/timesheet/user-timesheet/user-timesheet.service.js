(function() {
    'use strict';
    angular
        .module('cpmApp')
        .factory('UserTimesheet', UserTimesheet);

    UserTimesheet.$inject = ['$resource', 'DateUtils'];

    function UserTimesheet ($resource, DateUtils) {
        var resourceUrl =  'api/user-timesheets/:id';
        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.createTime = DateUtils.convertDateTimeFromServer(data.createTime);
                        data.updateTime = DateUtils.convertDateTimeFromServer(data.updateTime);
                        if(data.type == 1){
            				data.typeName = "公共成本";
            			}else if(data.type == 2){
            				data.typeName = "合同";
            			}else if(data.type == 3){
            				data.typeName = "项目";
            			}
            			if(data.status == 1){
            				data.statusName = "正常";
            			}else if(data.status == 2){
            				data.statusName = "删除";
            			}
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
