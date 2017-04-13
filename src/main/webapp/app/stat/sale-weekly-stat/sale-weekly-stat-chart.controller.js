(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('SaleWeeklyStatChartController', SaleWeeklyStatChartController);

    SaleWeeklyStatChartController.$inject = ['$scope','$state','DateUtils', '$rootScope', '$stateParams', 'pagingParams', 'SaleWeeklyStat', 'AlertService','previousState'];

    function SaleWeeklyStatChartController ($scope,$state,DateUtils,$rootScope, $stateParams, pagingParams, SaleWeeklyStat, AlertService,previousState) {
    	var vm = this;
        vm.transition = transition;
        vm.clear = clear;
        vm.loadAll = loadAll;
        vm.search = search;
        vm.searchQuery = {};
        var fromDate = pagingParams.fromDate;
        var toDate = pagingParams.toDate;
        if(fromDate && fromDate.length == 8){
        	fromDate = new Date(fromDate.substring(0,4),parseInt(fromDate.substring(4,6))-1,fromDate.substring(6,8));
        }
        if(toDate && toDate.length == 8){
        	toDate = new Date(toDate.substring(0,4),parseInt(toDate.substring(4,6))-1,toDate.substring(6,8));
        }
        vm.searchQuery.fromDate= fromDate;
        vm.searchQuery.toDate = toDate;
        vm.searchQuery.id = pagingParams.id;
        if (!vm.searchQuery.fromDate && !vm.searchQuery.toDate){
        	vm.haveSearch = null;
        }else{
        	vm.haveSearch = true;
        }
        loadAll();
        vm.previousState = previousState.name;
        function loadAll () {
        	var windowWidth = window.screen.width;
        	var gridHeight, gridY,legendY;
        	if(windowWidth <= 430){
            	gridHeight = '55%';
            	gridY = '40%';
            	legendY = '36';
            }else{
            	gridHeight = '65%';
            	gridY = '25%';
            	legendY = '32';
            }
        	if(pagingParams.fromDate == undefined){
        		pagingParams.fromDate = "";
        	};
        	if(pagingParams.toDate == undefined){
        		pagingParams.toDate = "";
        	};
        	if(pagingParams.id == undefined){
        		pagingParams.id = "";
        	};
        	SaleWeeklyStat.queryChart({
                fromDate : pagingParams.fromDate,
                toDate : pagingParams.toDate,
                id : pagingParams.id
            }, onSuccess, onError);
           function onSuccess(data, headers) {
        	   var series = [];
	       	   	for(var i = 0; i < 13 ; i++){
	       	   		series.push({//1
	                       name: '',
	                       type: '',
	                       data: [],
	                       markPoint: {
	                           data: [{
	                               type: 'max',
	                               name: '最大值'
	                           }
	                           ,{
	                               type: 'min',
	                               name: '最小值'
	                           }
	                           ]
	                       },
	                       markLine: {
	                           data: [{
	                               type: 'average',
	                               name: '平均值'
	                           }]
	                       }
	                   });
	       	   	} 
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
                        selected: {
                            '合同累计完成金额' : false,
                            '当年收款金额' : false,
                            '当年新增所有成本' : false,
                            '当年销售人工成本' : false,
                            '当年销售报销成本' : false,
                            '当年咨询人工成本' : false,
                            '当年咨询报销成本' : false,
                            '当年硬件成本' : false,
                            '当年外部软件成本' : false,
                            '当年内部软件成本' : false,
                            '当年项目人工成本' : false,
                            '当年项目报销成本' : false
                        },
                        y: legendY
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
                            formatter: '{value}'
                        },
                        splitArea: {
                            show: true
                        }
                    }],
                    grid: {
                        width: '80%',
                        
                        height : gridHeight,
                        y: gridY
                    },
                    series: series
                };

                var chartBottom = echarts.init(document.getElementById('tabcontent1'));

                option.legend.data = data.legend;
                option.title.text = data.title;
                option.xAxis[0].data = data.category;
                option.series[0].data = data.series[0].data;
                option.series[0].name = data.series[0].name;
                option.series[0].type = data.series[0].type;
                option.series[1].data = data.series[1].data;
                option.series[1].name = data.series[1].name;
                option.series[1].type = data.series[1].type;
                option.series[2].data = data.series[2].data;
                option.series[2].name = data.series[2].name;
                option.series[2].type = data.series[2].type;
                option.series[3].data = data.series[3].data;
                option.series[3].name = data.series[3].name;
                option.series[3].type = data.series[3].type;
                option.series[4].data = data.series[4].data;
                option.series[4].name = data.series[4].name;
                option.series[4].type = data.series[4].type;
                option.series[5].data = data.series[5].data;
                option.series[5].name = data.series[5].name;
                option.series[5].type = data.series[5].type;
                option.series[6].data = data.series[6].data;
                option.series[6].name = data.series[6].name;
                option.series[6].type = data.series[6].type;
                option.series[7].data = data.series[7].data;
                option.series[7].name = data.series[7].name;
                option.series[7].type = data.series[7].type;
                option.series[8].data = data.series[8].data;
                option.series[8].name = data.series[8].name;
                option.series[8].type = data.series[8].type;
                option.series[9].data = data.series[9].data;
                option.series[9].name = data.series[9].name;
                option.series[9].type = data.series[9].type;
                option.series[10].data = data.series[10].data;
                option.series[10].name = data.series[10].name;
                option.series[10].type = data.series[10].type;
                option.series[11].data = data.series[11].data;
                option.series[11].name = data.series[11].name;
                option.series[11].type = data.series[11].type;
                option.series[12].data = data.series[12].data;
                option.series[12].name = data.series[12].name;
                option.series[12].type = data.series[12].type;
                chartBottom.hideLoading();

                chartBottom.setOption(option);
                
            }
            function onError(error) {
                AlertService.error(error.data.message);
            }
        }
        
        function transition() {
            $state.transitionTo($state.$current, {
                fromDate: DateUtils.convertLocalDateToFormat(vm.searchQuery.fromDate,"yyyyMMdd"),
                toDate: DateUtils.convertLocalDateToFormat(vm.searchQuery.toDate,"yyyyMMdd"),
                id:vm.searchQuery.id ? vm.searchQuery.id : ""
            });
        }
        
        function clear() {
            vm.searchQuery.fromDate = null;
            vm.searchQuery.toDate = null;
            vm.haveSearch = null;
            vm.transition();
        }
        
        function search(searchQuery) {
        	if (!vm.searchQuery.fromDate && !vm.searchQuery.toDate){
                return vm.clear();
            }
        	vm.reverse = false;
            vm.haveSearch = true;
            vm.transition();
        }
        
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.datePickerOpenStatus.fromDate = false;
        vm.datePickerOpenStatus.toDate = false;
        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
