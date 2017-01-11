(function() {
    'use strict';

    angular
        .module('cpmApp')
        .factory('DateUtils', DateUtils);

    DateUtils.$inject = ['$filter'];

    function DateUtils ($filter) {

        var service = {
            convertDateTimeFromServer : convertDateTimeFromServer,
            convertLocalDateFromServer : convertLocalDateFromServer,
            convertLocalDateToServer : convertLocalDateToServer,
            convertLocalDateToFormat : convertLocalDateToFormat,
            convertYYYYMMDDDayToDate : convertYYYYMMDDDayToDate,
            convertDayToDate:convertDayToDate,
            dateformat : dateformat
        };

        return service;

        function convertDayToDate(date){
        	if(date){
        		date = date + "";
        		date = date.replace(new RegExp("-","gm"),"");
        		if(date.length == 8){
        			return new Date(date.substring(0,4),parseInt(date.substring(4,6))-1,date.substring(6,8));
        		}
        	}
        	return null;
        }
        function convertYYYYMMDDDayToDate (date){
        	if(date){
        		date = date + "";
        		if(date.length == 8){
        			return new Date(date.substring(0,4),parseInt(date.substring(4,6))-1,date.substring(6,8));
        		}
        	}
        	return null;
        }
        function convertDateTimeFromServer (date) {
            if (date) {
                return new Date(date);
            } else {
                return null;
            }
        }

        function convertLocalDateFromServer (date) {
            if (date) {
                var dateString = date.split('-');
                return new Date(dateString[0], dateString[1] - 1, dateString[2]);
            }
            return null;
        }

        function convertLocalDateToServer (date) {
            if (date) {
                return $filter('date')(date, 'yyyy-MM-dd');
            } else {
                return null;
            }
        }
        
        function convertLocalDateToFormat (date,format) {
            if (date) {
                return $filter('date')(date, format);
            } else {
                return null;
            }
        }
        
        function dateformat () {
            return 'yyyy-MM-dd';
        }
    }

})();
