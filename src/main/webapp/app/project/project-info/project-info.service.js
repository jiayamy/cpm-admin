(function() {
    'use strict';
    angular
        .module('cpmApp')
        .factory('ProjectInfo', ProjectInfo);

    ProjectInfo.$inject = ['$resource', 'DateUtils'];

    function ProjectInfo ($resource, DateUtils) {
        var resourceUrl =  'api/project-infos/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        
                        data.startDay = DateUtils.convertDateTimeFromServer(data.startDay);
                        data.endDay = DateUtils.convertDateTimeFromServer(data.endDay);
                        data.createTime = DateUtils.convertDateTimeFromServer(data.createTime);
                        data.updateTime = DateUtils.convertDateTimeFromServer(data.updateTime);

                        if(data.status == 1){
            				data.statusName = "开发中";
            			}else if(data.status == 2){
            				data.statusName = "已结项";
            			}else if(data.status == 3){
            				data.statusName = "已删除";
            			}
                    }
                    return data;
                }
            },
            'update': { method:'PUT' },
            'queryUserContract':{
            	url:'api/project-infos/queryUserContract',
            	method:'GET',
            	isArray:true
            },
            'queryUserContractBudget':{
            	url:'api/project-infos/queryUserContractBudget',
            	method:'GET',
            	isArray:true
            },
            'end':{
            	url:'api/project-infos/end',
            	method:'GET',
            	isArray:true
            },
            'finish':{
            	url:'api/project-infos/finish',
            	method:'GET',
            	isArray:true
            }
        });
    }
})();
