(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ContractInfoController', ContractInfoController);

    ContractInfoController.$inject = ['$rootScope','$http','$scope', '$state', 'ContractInfo', 'ContractInfoSearch', 'ParseLinks', 'AlertService', 'paginationConstants', 'pagingParams'];

    function ContractInfoController ($rootScope, $http, $scope, $state, ContractInfo, ContractInfoSearch, ParseLinks, AlertService, paginationConstants, pagingParams) {
        var vm = this;

        vm.loadPage = loadPage;
        vm.predicate = pagingParams.predicate;
        vm.reverse = pagingParams.ascending;
        vm.transition = transition;
        vm.itemsPerPage = paginationConstants.itemsPerPage;
        vm.clear = clear;
        vm.search = search;
        vm.loadAll = loadAll;
        vm.searchQuery = {};
        
        var name = pagingParams.name;
        var type = pagingParams.type;
        var isPrepared = pagingParams.isPrepared;
        var isEpibolic = pagingParams.isEpibolic;
        var salesman = pagingParams.salesman;
        if(type){
        	if(type == 1){
        		type = { id: 1, name: '产品合同' };
        	}else if(type == 2){
        		type = { id: 2, name: '外包合同' };
        	}else if(type == 3){
        		type = { id: 3, name: '硬件合同' };
        	}else if(type == 4){
        		type = { id: 4, name: '公共成本' };
        	}
        }
        if(isPrepared != null){
        	if(isPrepared == true){
        		isPrepared = { id: 1, name: '正式合同' };
        	}else if(isPrepared == false){
        		isPrepared = { id: 0, name: '预立合同' };
        	}
        }
        if(isEpibolic != null){
        	if(isEpibolic == true){
        		isEpibolic = { id: 1, name: '外包合同' };
        	}else if(isEpibolic == false){
        		isEpibolic = { id: 0, name: '非外包合同'};
        	}
        }
        	
        	
        //控制回显
        vm.searchQuery.name=name;
        vm.searchQuery.type=type;
        vm.searchQuery.isPrepared=isPrepared;
        vm.searchQuery.isEpibolic=isEpibolic;
        vm.searchQuery.salesman=salesman;
       
        vm.types = [{ id: 1, name: '产品合同' }, { id: 2, name: '外包合同' },{ id: 3, name: '硬件合同' },{ id: 4, name: '公共成本' }];
        vm.isPrepareds = [{ id: 1, name: '正式合同'}, { id: 0, name: '预立合同'}];
        vm.isEpibolics = [{ id: 1, name: '外包合同'}, { id: 0, name: '非外包合同'}];
        if(!vm.searchQuery.name && !vm.searchQuery.type &&  !vm.searchQuery.isPrepared){
        	vm.haveSearch = null;
        }else {
			vm.haveSearch = true;
		}
        
        loadAll();
        function loadAll () {
        	if(pagingParams.name == undefined){
        		pagingParams.name="";
        	}
        	if(pagingParams.type == undefined){
        		pagingParams.type="";
        	}
        	if(pagingParams.isPrepared == undefined){
        		pagingParams.isPrepared="";
        	}
        	if(pagingParams.isEpibolic == undefined){
        		pagingParams.isEpibolic="";
        	}
        	if(pagingParams.salesman == undefined){
        		pagingParams.salesman="";
        	}
	    	ContractInfo.query({
	    		 name : pagingParams.name,
	    		 type : pagingParams.type,
	    		 isPrepared : pagingParams.isPrepared,
	    		 isEpibolic : pagingParams.isEpibolic,
	    		 salesman : pagingParams.salesman,
	             page: pagingParams.page - 1,
	             size: vm.itemsPerPage,
	             sort: sort()
	         }, onSuccess, onError);

	    	function sort() {
                var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
                if (vm.predicate !== 'id') {
                    result.push('id');
                }
                return result;
            }
            function onSuccess(data, headers) {
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
                vm.queryCount = vm.totalItems;
                vm.contractInfos = handleData(data);
                vm.page = pagingParams.page;
            }
            function onError(error) {
                AlertService.error(error.data.message);
            }
        }
        function handleData(data){
        	if(data.length>0){
        		for (var i = 0; i< data.length; i++) {
        			//type的枚举
					if(data[i].type==1){
						data[i].type="产品合同";
					}else if (data[i].type==2) {
						data[i].type="外包合同";
					}else if (data[i].type==3) {
						data[i].type="硬件合同";
					}else if (data[i].type==4) {
						data[i].type="公共成本";
					}
					//status的枚举
					if(data[i].status == 1){
        				data[i].status = "可用";
        			}else if(data[i].status == 2){
        				data[i].status = "完成";
        			}else if(data[i].status == 3){
        				data[i].status = "删除";
        			}
					//是否预立
					if(data[i].isPrepared == true){
						data[i].isPrepared = "正式合同"
					}else if (data[i].isPrepared == false) {
						data[i].isPrepared = "预立合同"
					}
					//是否外包
					if(data[i].isEpibolic == true){
						data[i].isEpibolic = "外包"
					}else if (data[i].isEpibolic == false) {
						data[i].isEpibolic = "非外包"
					}
				}
        	}
        	return data;
        }

        function loadPage(page) {
            vm.page = page;
            vm.transition();
        }

        function transition() {
            $state.transitionTo($state.$current, {
                page: vm.page,
                sort: vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc'),
                name:vm.searchQuery.name,
                type: vm.searchQuery.type != null ? vm.searchQuery.type.id : "",
                isPrepared:vm.searchQuery.isPrepared,
                isEpibolic:vm.searchQuery.isEpibolic,
                salesman:vm.searchQuery.salesman
            });
        }

        function search() {
            if (!vm.searchQuery.name && !vm.searchQuery.type && !vm.searchQuery.isPrepared && !vm.searchQuery.isEpibolic && !vm.searchQuery.salesman){
                return vm.clear();
            }
            //转换成boolean值
            if(vm.searchQuery.isEpibolic){
            	if(vm.searchQuery.isEpibolic.id == 1){
                	vm.searchQuery.isEpibolic=true;
                }else if (vm.searchQuery.isEpibolic.id == 0) {
                	vm.searchQuery.isEpibolic = false;
    			}
            }
            if(vm.searchQuery.isPrepared){
            	if(vm.searchQuery.isPrepared.id == 1){
                	vm.searchQuery.isPrepared = true;
                }else if (vm.searchQuery.isPrepared.id == 0) {
                	vm.searchQuery.isPrepared =false;
    			}
            }
            vm.links = null;
            vm.page = 1;
            vm.predicate = 'updateTime';
            vm.reverse = false;
            vm.haveSearch = true;
            vm.transition();
            if(isPrepared != null){
            	if(isPrepared == true){
            		vm.searchQuery.isPrepared = { id: 1, name: '正式合同' };
            	}else if(isPrepared == false){
            		vm.searchQuery.isPrepared = { id: 0, name: '预立合同' };
            	}
            }
            if(isEpibolic != null){
            	if(isEpibolic == true){
            		vm.searchQuery.isEpibolic = { id: 1, name: '外包合同' };
            	}else if(isEpibolic == false){
            		vm.searchQuery.isEpibolic = { id: 0, name: '非外包合同'};
            	}
            }
            console.log(vm.searchQuery.isPrepared);
        }

        function clear() {
            vm.links = null;
            vm.page = 1;
            vm.predicate = 'updateTime';
            vm.reverse = false;
            vm.searchQuery = {};
            vm.haveSearch = null;
            vm.transition();
        }
        //员工模态框
        var unsubscribe = $rootScope.$on('cpmApp:deptInfoSelected', function(event, result) {
        	vm.searchQuery.salesman = result.objId;
        	console.log(result);
        });
        
    }
})();
