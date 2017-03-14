(function(){
	'use strict';

    angular
        .module('cpmApp')
        .controller('ConsultantBonusContractRecordController', ConsultantBonusContractRecordController);

    ConsultantBonusContractRecordController.$inject = ['$state','ConsultantBonus','ParseLinks', 'AlertService','paginationConstants', 'pagingParams', 'previousState'];

    function ConsultantBonusContractRecordController($state, ConsultantBonus, ParseLinks, AlertService, paginationConstants, pagingParams, previousState){
    	var vm = this;

    	vm.loadPage = loadPage;
        vm.predicate = pagingParams.predicate;
        vm.reverse = pagingParams.ascending;
        vm.itemsPerPage = paginationConstants.itemsPerPage;
        vm.previousState = previousState.name;
        vm.transition = transition;
        vm.back = back;
        vm.loadAll = loadAll;
        
        vm.backParams = {};	//返回时所需参数
        vm.backParams.contractId = pagingParams.contractId;
        vm.backParams.consultantsId = pagingParams.consultantsId;
        vm.backParams.statWeek = pagingParams.statWeek;
        vm.backParams.consultantsMan = pagingParams.consultantsMan;
        
        loadAll();

        function loadAll () {
        	ConsultantBonus.queryConsultantRecord({
                page: pagingParams.page-1,
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
                contId : pagingParams.contId,
                contractId: pagingParams.contractId,
                consultantsId : pagingParams.consultantsId,
                statWeek : pagingParams.statWeek,
                consultantsName: pagingParams.consultantsName
            });
        }
        function back(){
        	$state.go('consultant-bonus',vm.backParams,null);
        }
    }
})();