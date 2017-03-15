(function(){
	'use strict';

    angular
        .module('cpmApp')
        .controller('ConsultantBonusContractRecordController', ConsultantBonusContractRecordController);

    ConsultantBonusContractRecordController.$inject = ['$state','ConsultantBonus','ParseLinks', 'AlertService','paginationConstants', 'pagingParams', 'previousState'];

    function ConsultantBonusContractRecordController($state, ConsultantBonus, ParseLinks, AlertService, paginationConstants, pagingParams, previousState){
    	var vm = this;

        vm.itemsPerPage = paginationConstants.itemsPerPage;
        vm.previousState = previousState.name;
        vm.loadAll = loadAll;
        vm.page = 1;
        
        loadAll();

        function loadAll () {
        	ConsultantBonus.queryConsultantRecord({
        		page:vm.page - 1,
                size: vm.itemsPerPage,
                contId : pagingParams.contId
            }, onSuccess, onError);
           
            function onSuccess(data, headers) {
                vm.links = ParseLinks.parse(headers('link'));
                vm.totalItems = headers('X-Total-Count');
                vm.queryCount = vm.totalItems;
                vm.consultantBonuss = data;
            }
            function onError(error) {
                AlertService.error(error.data.message);
            }
        }
    }
})();