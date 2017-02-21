(function(){
	'use strict';

    angular
        .module('cpmApp')
        .controller('ConsultantBonusContractRecordController', ConsultantBonusContractRecordController);

    ConsultantBonusContractRecordController.$inject = ['$state','ConsultantBonus','ParseLinks', 'AlertService', 'pagingParams', 'previousState'];

    function ConsultantBonusContractRecordController($state, ConsultantBonus, ParseLinks, AlertService, pagingParams, previousState){
    	var vm = this;

        vm.loadPage = loadPage;
        vm.predicate = pagingParams.predicate;
        vm.reverse = pagingParams.ascending;
        vm.transition = transition;
        vm.itemsPerPage = 10;
        vm.previousState = previousState.name;
        vm.loadAll = loadAll;
        vm.exportXls = exportXls;
        
        loadAll();

        function loadAll () {
        	ConsultantBonus.queryConsultantRecord({
                page: pagingParams.page - 1,
                size: vm.itemsPerPage,
                sort: sort(),
                contId : pagingParams.contId
            }, onSuccess, onError);
           
            function sort() {
                var result = [vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc')];
                if (vm.predicate !== 'm.id') {
                    result.push('m.id');
                }
                return result;
            }
            function onSuccess(data, headers) {
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
                vm.queryCount = vm.totalItems;
                vm.consultantBonuss = data;
                vm.page = pagingParams.page;
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
                page: vm.page,
                sort: vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc'),
                contractId:vm.searchQuery.contractId ? vm.searchQuery.contractId.key : ""
            });
        }
        
        function exportXls(){
        	var url = "api/consultant-bonus/contractRecord/exportXls";
        	var c = 0;
        	var contractId = pagingParams.contId;
			
			if(contractId){
				if(c == 0){
					c++;
					url += "?";
				}else{
					url += "&";
				}
				url += "contractId="+encodeURI(contractId);
			}
        	window.open(url);
        }
    }
})();