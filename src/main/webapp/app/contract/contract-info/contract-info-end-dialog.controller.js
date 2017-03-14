(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ContractInfoEndController',ContractInfoEndController);

    ContractInfoEndController.$inject = ['$uibModalInstance', 'entity', 'ContractInfo'];

    function ContractInfoEndController($uibModalInstance, entity, ContractInfo) {
        var vm = this;

        vm.contractInfo = entity;
        vm.clear = clear;
        vm.confirmEnd = confirmEnd;

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmEnd (id) {
            ContractInfo.end({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
