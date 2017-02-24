(function() {
    'use strict';

    angular
        .module('cpmApp')
        .constant('paginationConstants', {
            'itemsPerPage': 20,
            "itemsPerPage_10":10,
            "itemsPerPage_50":50,
            "itemsPerPage_100":100,
            "itemsPerPage_500":500,
            "itemsPerPage_1000":1000,
        });
})();
