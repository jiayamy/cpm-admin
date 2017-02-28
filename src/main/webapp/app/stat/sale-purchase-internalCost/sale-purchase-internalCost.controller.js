(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('SalePurchaseInternalCostController', SalePurchaseInternalCostController);

    SalePurchaseInternalCostController.$inject = ['ContractInfo','DeptType','$rootScope', '$scope', '$state', 'DateUtils','paginationConstants','SalePurchaseInternalCost','ParseLinks', 'AlertService', 'pagingParams'];

    function SalePurchaseInternalCostController (ContractInfo,DeptType,$rootScope,$scope, $state,DateUtils,paginationConstants, SalePurchaseInternalCost, ParseLinks, AlertService, pagingParams) {
        var vm = this;

        vm.loadPage = loadPage;
        vm.transition = transition;
        vm.loadAll = loadAll;
        vm.clear = clear;
        vm.search = search;
        vm.loadAll = loadAll;
        vm.searchQuery = {};
        vm.exportXls = exportXls;
        
        vm.searchQuery.statWeek = DateUtils.convertYYYYMMDDDayToDate(pagingParams.statWeek);
        vm.searchQuery.contractId = pagingParams.contractId;
        vm.searchQuery.userNameId = pagingParams.userNameId;
        vm.searchQuery.userName = pagingParams.userName;
        vm.searchQuery.deptType = pagingParams.deptType;
        vm.contractInfos = [];
        
        if (!vm.searchQuery.contractId && !vm.searchQuery.userNameId && !vm.searchQuery.statWeek && !vm.searchQuery.deptType){
			vm.haveSearch = null;
		}else{
			vm.haveSearch = true;
		}
        
        //部门类型
        loadDeptType();
        function loadDeptType(){
        	DeptType.getAllForCombox(
    			{
        		},
        		function(data, headers){
        			vm.deptTypes = data;
            		if(vm.deptTypes && vm.deptTypes.length > 0){
            			for(var i = 0; i < vm.deptTypes.length; i++){
            				if(pagingParams.deptType == vm.deptTypes[i].key){
            					vm.searchQuery.deptType = vm.deptTypes[i];
            				}
            			}
            		}
        		},
        		function(error){
        			AlertService.error(error.data.message);
        		}
        	);
        }
        //合同信息
        loadContractInfo();
        function loadContractInfo(){
        	ContractInfo.queryContractInfo({
        		
        	},
        	function(data, headers){
        		vm.contractInfos = data;
        		if(vm.contractInfos && vm.contractInfos.length > 0){
        			for(var i = 0; i < vm.contractInfos.length; i++){
        				if(pagingParams.contractId == vm.contractInfos[i].key){
        					vm.searchQuery.contractId = vm.contractInfos[i];
        				}
        			}
        			if(!pagingParams.contractId){
        				pagingParams.contractId = vm.contractInfos[0].key;
        				vm.searchQuery.contractId = vm.contractInfos[0];
        			}
        			loadAll();
        		}
        	},
        	function(error){
        		AlertService.error(error.data.message);
        	});
        }
        
//        if(vm.searchQuery.contractId){
//        	loadAll();
//		}

        function loadAll () {
        	SalePurchaseInternalCost.query({
                contractId : pagingParams.contractId,
                userNameId : pagingParams.userNameId,
                statWeek : pagingParams.statWeek,
                deptType : pagingParams.deptType
            }, onSuccess, onError);
           
            function onSuccess(data, headers) {
                vm.salePurchaseInternalCosts = data;
//            	$rootScope.backDetail = {	//为详情页提供返回参数
//            			contractId : pagingParams.contractId,
//                        userNameId : pagingParams.userNameId,
//                        userName : pagingParams.userName,
//                        statWeek : pagingParams.statWeek,
//                        deptType : pagingParams.deptType
//            	};
//            	console.log($rootScope.backDetail);
            }
            function onError(error) {
                AlertService.error(error.data.message);
            }
        }

        function loadPage(page) {
            vm.page = page;
            vm.transition();
        }

        function transition() {
            $state.transitionTo($state.$current, {
                contractId:vm.searchQuery.contractId ? vm.searchQuery.contractId.key : "",
                userNameId:vm.searchQuery.userNameId,
                userName:vm.searchQuery.userName,
                statWeek:vm.searchQuery.statWeek ? DateUtils.convertLocalDateToFormat(vm.searchQuery.statWeek,"yyyyMMdd"):"",
                deptType:vm.searchQuery.deptType ? vm.searchQuery.deptType.key : ""
            });
        }
        
        function search() {
        	if(!vm.searchQuery.contractId){
        		AlertService.error("cpmApp.salePurchaseInternalCost.error.contractSerialNumNon");
        	}
        	if (!vm.searchQuery.contractId && !vm.searchQuery.statWeek && !vm.searchQuery.userName && !vm.searchQuery.deptType){
                return vm.clear();
            }
            vm.haveSearch = true;
            vm.transition();
        }

        function clear() {
            vm.searchQuery = {};
            vm.haveSearch = false;
            vm.transition();
        }
        
        function exportXls(){
        	var url = "api/sale-purchase-internalCost/exportXls";
        	var c = 0;
        	var statWeek = DateUtils.convertLocalDateToFormat(vm.searchQuery.statWeek,"yyyyMMdd");
        	var contractId = vm.searchQuery.contractId && vm.searchQuery.contractId.key? vm.searchQuery.contractId.key : vm.searchQuery.contractId;
        	var userNameId = vm.searchQuery.userNameId;
        	var deptType = vm.searchQuery.deptType ? vm.searchQuery.deptType.key : vm.searchQuery.deptType;
			
        	if(!vm.searchQuery.contractId){
        		AlertService.error("cpmApp.salePurchaseInternalCost.error.contractSerialNumNon");
        		return ;
        	}
        	
			if(statWeek){
				if(c == 0){
					c++;
					url += "?";
				}else{
					url += "&";
				}
				url += "statWeek="+encodeURI(statWeek);
			}
			if(contractId){
				if(c == 0){
					c++;
					url += "?";
				}else{
					url += "&";
				}
				url += "contractId="+encodeURI(contractId);
			}
			if(userNameId){
				if(c == 0){
					c++;
					url += "?";
				}else{
					url += "&";
				}
				url += "userNameId="+encodeURI(userNameId);
			}
			if(deptType){
				if(c == 0){
					c++;
					url += "?";
				}else{
					url += "&";
				}
				url += "deptType="+encodeURI(deptType);
			}
			
        	window.open(url);
        }
        
        vm.datePickerOpenStatus = {};
        vm.datePickerOpenStatus.statWeek = false;
        vm.openCalendar = openCalendar;
        function openCalendar(data){
        	vm.datePickerOpenStatus[data] = true;
        }
        
        var unsubscribe = $rootScope.$on('cpmApp:deptInfoSelected', function(event, result) {
        	vm.searchQuery.userNameId = result.objId;
        	vm.searchQuery.userName = result.name;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
