(function () {
    'use strict';

    angular
        .module('cpmApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig ($stateProvider) {
        $stateProvider.state('timesheet', {
            abstract: true,
            parent: 'app'
        });
    }
})();
