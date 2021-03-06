(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('SalePurchaseInternalCostController', SalePurchaseInternalCostController);

    SalePurchaseInternalCostController.$inject = ['ProjectInfo','DeptType','$rootScope', '$scope', '$state', 'DateUtils','paginationConstants','SalePurchaseInternalCost','ParseLinks', 'AlertService', 'pagingParams'];

    function SalePurchaseInternalCostController (ProjectInfo,DeptType,$rootScope,$scope, $state,DateUtils,paginationConstants, SalePurchaseInternalCost, ParseLinks, AlertService, pagingParams) {
        var vm = this;

        vm.loadPage = loadPage;
        vm.transition = transition;
        vm.loadAll = loadAll;
        vm.clear = clear;
        vm.search = search;
        vm.loadAll = loadAll;
        vm.searchQuery = {};
        vm.exportXls = exportXls;
        
        var today = new Date();
        if(pagingParams.statWeek == undefined){
        	pagingParams.statWeek = DateUtils.convertLocalDateToFormat(today,"yyyyMMdd");
        }
        
        vm.searchQuery.statWeek = DateUtils.convertYYYYMMDDDayToDate(pagingParams.statWeek);
        vm.searchQuery.contractId = pagingParams.contractId;
        vm.searchQuery.userId = pagingParams.userId;
        vm.searchQuery.userName = pagingParams.userName;
        vm.searchQuery.deptType = pagingParams.deptType;
        vm.contractInfos = [];
        
        if (!vm.searchQuery.contractId && !vm.searchQuery.userId && !vm.searchQuery.statWeek && !vm.searchQuery.deptType){
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
        	ProjectInfo.queryUserContract({
        		
        	},
        	function(data, headers){
        		vm.contractInfos = data;
        		if(vm.contractInfos && vm.contractInfos.length > 0){
        			for(var i = 0; i < vm.contractInfos.length; i++){
        				if(pagingParams.contractId == vm.contractInfos[i].key){
        					vm.searchQuery.contractId = vm.contractInfos[i];
        					angular.element('select[ng-model="vm.searchQuery.contractId"]').parent().find(".select2-chosen").html(vm.contractInfos[i].val);
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
        
        function loadAll () {
        	SalePurchaseInternalCost.query({
                contractId : pagingParams.contractId,
                userId : pagingParams.userId,
                statWeek : pagingParams.statWeek,
                deptType : pagingParams.deptType
            }, onSuccess, onError);
           
            function onSuccess(data, headers) {
                vm.salePurchaseInternalCosts = data;
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
                userId:vm.searchQuery.userId,
                userName:vm.searchQuery.userName,
                statWeek:DateUtils.convertLocalDateToFormat(vm.searchQuery.statWeek,"yyyyMMdd"),
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
            vm.searchQuery.statWeek = new Date();
            vm.haveSearch = true;
            vm.transition();
        }
        
        function exportXls(){
        	var url = "api/sale-purchase-internalCost/exportXls";
        	var c = 0;
        	var statWeek = DateUtils.convertLocalDateToFormat(vm.searchQuery.statWeek,"yyyyMMdd");
        	var contractId = vm.searchQuery.contractId && vm.searchQuery.contractId.key? vm.searchQuery.contractId.key : vm.searchQuery.contractId;
        	var userId = vm.searchQuery.userId;
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
			if(userId){
				if(c == 0){
					c++;
					url += "?";
				}else{
					url += "&";
				}
				url += "userId="+encodeURI(userId);
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
        	vm.searchQuery.userId = result.objId;
        	vm.searchQuery.userName = result.name;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
