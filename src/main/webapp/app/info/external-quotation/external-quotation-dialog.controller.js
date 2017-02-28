(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ExternalQuotationDialogController', ExternalQuotationDialogController);

    ExternalQuotationDialogController.$inject = ['$scope', '$state', '$stateParams', 'previousState', 'entity', 'ExternalQuotation','AlertService'];

    function ExternalQuotationDialogController ($scope, $state, $stateParams, previousState, entity, ExternalQuotation,AlertService) {
        var vm = this;

        vm.externalQuotation = entity;
        
        vm.gradeDisable = vm.externalQuotation.id;
        
        vm.previousState = previousState.name;
        vm.save = save;
        
        vm.externalQuotationChanged = externalQuotationChanged;
        vm.socialSecurityFundChanged = socialSecurityFundChanged;
        vm.otherExpenseChanged = otherExpenseChanged;
        
        //对外报价变动
        function externalQuotationChanged(){
        	if(vm.externalQuotation.externalQuotation == undefined){
        		vm.externalQuotation.externalQuotation = 0;
        	}
        	//社保公积金44%
        	vm.externalQuotation.socialSecurityFund = vm.externalQuotation.externalQuotation * 0.44;
        	//其他费用10%
        	vm.externalQuotation.otherExpense = vm.externalQuotation.externalQuotation * 0.10;
        	//成本依据 三个费用加起来
        	vm.externalQuotation.costBasis = vm.externalQuotation.externalQuotation + vm.externalQuotation.socialSecurityFund
        		+ vm.externalQuotation.otherExpense;
        	//标准工时 = 成本依据 /168;
        	vm.externalQuotation.hourCost = vm.externalQuotation.costBasis / 168;
        	
        	//取小数点后2位
        	vm.externalQuotation.externalQuotation = Math.round(vm.externalQuotation.externalQuotation*100)/100;
        	vm.externalQuotation.socialSecurityFund = Math.round(vm.externalQuotation.socialSecurityFund*100)/100;
        	vm.externalQuotation.otherExpense = Math.round(vm.externalQuotation.otherExpense*100)/100;
        	vm.externalQuotation.costBasis = Math.round(vm.externalQuotation.costBasis*100)/100;
        	vm.externalQuotation.hourCost = Math.round(vm.externalQuotation.hourCost*100)/100;
        }
        function socialSecurityFundChanged(){
        	if(vm.externalQuotation.externalQuotation == undefined){
        		vm.externalQuotation.externalQuotation = 0;
        	}
        	//社保公积金44%
        	if(vm.externalQuotation.socialSecurityFund == undefined){
        		vm.externalQuotation.socialSecurityFund = vm.externalQuotation.externalQuotation * 0.44;
        	}
        	//其他费用10%
        	if(vm.externalQuotation.otherExpense == undefined){
        		vm.externalQuotation.otherExpense = vm.externalQuotation.externalQuotation * 0.10;
        	}
        	//成本依据 三个费用加起来
        	vm.externalQuotation.costBasis = vm.externalQuotation.externalQuotation + vm.externalQuotation.socialSecurityFund
        		+ vm.externalQuotation.otherExpense;
        	//标准工时 = 成本依据 /168;
        	vm.externalQuotation.hourCost = vm.externalQuotation.costBasis / 168;
        	
        	//取小数点后2位
        	vm.externalQuotation.externalQuotation = Math.round(vm.externalQuotation.externalQuotation*100)/100;
        	vm.externalQuotation.socialSecurityFund = Math.round(vm.externalQuotation.socialSecurityFund*100)/100;
        	vm.externalQuotation.otherExpense = Math.round(vm.externalQuotation.otherExpense*100)/100;
        	vm.externalQuotation.costBasis = Math.round(vm.externalQuotation.costBasis*100)/100;
        	vm.externalQuotation.hourCost = Math.round(vm.externalQuotation.hourCost*100)/100;
        }
        function otherExpenseChanged(){
        	if(vm.externalQuotation.externalQuotation == undefined){
        		vm.externalQuotation.externalQuotation = 0;
        	}
        	//社保公积金44%
        	if(vm.externalQuotation.socialSecurityFund == undefined){
        		vm.externalQuotation.socialSecurityFund = vm.externalQuotation.externalQuotation * 0.44;
        	}
        	//其他费用10%
        	if(vm.externalQuotation.otherExpense == undefined){
        		vm.externalQuotation.otherExpense = vm.externalQuotation.externalQuotation * 0.10;
        	}
        	//成本依据 三个费用加起来
        	vm.externalQuotation.costBasis = vm.externalQuotation.externalQuotation + vm.externalQuotation.socialSecurityFund
        		+ vm.externalQuotation.otherExpense;
        	//标准工时 = 成本依据 /168;
        	vm.externalQuotation.hourCost = vm.externalQuotation.costBasis / 168;
        	//取小数点后2位
        	vm.externalQuotation.externalQuotation = Math.round(vm.externalQuotation.externalQuotation*100)/100;
        	vm.externalQuotation.socialSecurityFund = Math.round(vm.externalQuotation.socialSecurityFund*100)/100;
        	vm.externalQuotation.otherExpense = Math.round(vm.externalQuotation.otherExpense*100)/100;
        	vm.externalQuotation.costBasis = Math.round(vm.externalQuotation.costBasis*100)/100;
        	vm.externalQuotation.hourCost = Math.round(vm.externalQuotation.hourCost*100)/100;
        	
        }
        function save () {
            vm.isSaving = true;
            var externalQuotation = {};
            externalQuotation.id = vm.externalQuotation.id;
            externalQuotation.grade = vm.externalQuotation.grade;
            externalQuotation.externalQuotation = vm.externalQuotation.externalQuotation;
            externalQuotation.socialSecurityFund = vm.externalQuotation.socialSecurityFund;
            externalQuotation.otherExpense = vm.externalQuotation.otherExpense;
            externalQuotation.costBasis = vm.externalQuotation.costBasis;
            externalQuotation.hourCost = vm.externalQuotation.hourCost;

            ExternalQuotation.update(externalQuotation,
	    		function(data, headers){
            		vm.isSaving = false;
//            		AlertService.error(error.data.message);
            		if(headers("X-cpmApp-alert") == 'cpmApp.externalQuotation.updated'){
            			$state.go(vm.previousState);
            		}
	        	},
	        	function(error){
	        		vm.isSaving = false;
//	        		AlertService.error(error.data.message);
	        	}
	        );            
        }
    }
})();
