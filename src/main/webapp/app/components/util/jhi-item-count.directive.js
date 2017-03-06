(function() {
    'use strict';

    var jhiItemCount = {
        template: '<div class="info">' +
                    '当前 {{$ctrl.page ? ($ctrl.queryCount == 0 ? 0 : ((($ctrl.page - 1) * $ctrl.itemsPerPage) == 0 ? 1 : (($ctrl.page - 1) * $ctrl.itemsPerPage + 1))) : 0}} - ' +
                    '{{$ctrl.page ? (($ctrl.page * $ctrl.itemsPerPage) < $ctrl.queryCount ? ($ctrl.page * $ctrl.itemsPerPage) : $ctrl.queryCount) : 0}} ' +
                    '总计 {{$ctrl.queryCount ? $ctrl.queryCount : 0}} 条' +
                '</div>',
        bindings: {
            page: '<',
            queryCount: '<total',
            itemsPerPage: '<'
        }
    };

    angular
        .module('cpmApp')
        .component('jhiItemCount', jhiItemCount);
})();
