(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('RoleHardWorkingChartController', RoleHardWorkingChartController);

    RoleHardWorkingChartController.$inject = ['$scope','$state','DateUtils', '$rootScope', '$stateParams', 'pagingParams', 'RoleHardWorkingChart', 'AlertService','previousState'];

    function RoleHardWorkingChartController ($scope,$state,DateUtils,$rootScope, $stateParams, pagingParams, RoleHardWorkingChart, AlertService,previousState) {
    	var vm = this;
        vm.transition = transition;
        vm.clear = clear;
        vm.loadAll = loadAll;
        vm.search = search;
        vm.searchQuery = {};
        var date = pagingParams.beginningMonth;
        if(date && date.length == 6){
        	date = new Date(date.substring(0,4),parseInt(date.substring(4,6)),date.substring(6,8));
        }
        vm.searchQuery.beginningMonth= date;
        vm.searchQuery.id = pagingParams.id;
        
        if (!vm.searchQuery.beginningMonth){
        	vm.haveSearch = null;
        }else{
        	vm.haveSearch = true;
        }
        loadAll();
        vm.previousState = previousState.name;
         function loadAll () {
        	if(pagingParams.beginningMonth == undefined){
        		pagingParams.beginningMonth = "";
        	};
        	if(pagingParams.id == undefined){
        		pagingParams.id = "";
        	};
        	RoleHardWorkingChart.queryChart({
        		beginningMonth : pagingParams.beginningMonth,
                id : pagingParams.id
            }, onSuccess, onError);
           function onSuccess(data, headers) {
        	 //第三方参数设置
               var option = {
                   title: {
                       text: "订单数",
                       subtext: "",
                       sublink: ""
                   },
                   tooltip: {
                       trigger: 'axis'
                   },
                   legend: {
                       data: [],
                       y:'30'
                   },
                   toolbox: {
                       show: true,
                       feature: {
                           saveAsImage: {
                               show: true
                           }
                       }
                   },
                   calculable: true,
                   xAxis: [{
                       type: 'category',
                       boundaryGap: false,
                       data: []
                   }],
                   yAxis: [{
                       type: 'value',
                       axisLabel: {
                           formatter: '{value}%'
                       },
                       splitArea: {
                           show: true
                       }
                   }],
                   grid: {
                       width: '80%'
                   },
                   series: [
                       {
                           name: '',
                           type: '',
                           data: [],//必须是Integer类型的,String计算平均值会出错
                           markPoint: {
                               data: [{
                                   type: 'max',
                                   name: '最大值'
                               }, {
                                   type: 'min',
                                   name: '最小值'
                               }]
                           }
                       }
                   ]
               };

                var chartBottom = echarts.init(document.getElementById('tabcontent1'));

                option.legend.data = data.legend;
                option.title.text = data.title;
                option.xAxis[0].data = data.category;
                option.series[0].data = data.series[0].data;
                option.series[0].name = data.series[0].name;
                option.series[0].type = data.series[0].type
                chartBottom.hideLoading();
                chartBottom.setOption(option);
                
            }
            function onError(error) {
                AlertService.error(error.data.message);
            }
        }

        function transition() {
            $state.transitionTo($state.$current, {
            	beginningMonth: DateUtils.convertLocalDateToFormat(vm.searchQuery.beginningMonth,"yyyyMM"),
                id:vm.searchQuery.id ? vm.searchQuery.id : ""
            });
        }
        
        vm.backDetail = backDetail;
        function backDetail(){
        	$state.go('role-hardWorking.chart', {
        		id: pagingParams.id
            });
        }
        
        function clear() {
        	var today = new Date();
            if(date){
            	date = DateUtils.convertLocalDateToFormat(new Date(),"yyyyMM");
            	date = new Date(date.substring(0,4),parseInt(date.substring(4,6))-1,0);
            }
        	vm.searchQuery.beginningMonth = date;
            vm.transition();
        }
        function search(searchQuery) {
        	if (!vm.searchQuery.beginningMonth){
                return vm.clear();
            }
        	vm.reverse = false;
            vm.haveSearch = true;
            vm.transition();
        }
        
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.datePickerOpenStatus.beginningMonth = false;
        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
