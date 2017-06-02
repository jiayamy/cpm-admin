(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ProjectWeeklyStatChartController', ProjectWeeklyStatChartController);

    ProjectWeeklyStatChartController.$inject = ['$scope','$state','DateUtils', '$rootScope', '$stateParams', 'pagingParams', 'ProjectWeeklyStatChart', 'AlertService','previousState'];

    function ProjectWeeklyStatChartController ($scope,$state,DateUtils,$rootScope, $stateParams, pagingParams, ProjectWeeklyStatChart, AlertService,previousState) {
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
        	if(pagingParams.fromDate == undefined){
        		pagingParams.fromDate = "";
        	};
        	if(pagingParams.toDate == undefined){
        		pagingParams.toDate = "";
        	};
        	if(pagingParams.id == undefined){
        		pagingParams.id = "";
        	};
        	ProjectWeeklyStatChart.queryChart({
                fromDate : pagingParams.fromDate,
                toDate : pagingParams.toDate,
                id : pagingParams.id
            }, onLoadSuccess, onLoadError);
           function onLoadSuccess(data, headers) {
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
                        y :'30'
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
                            },
                            markLine: {
                                data: [{
                                    type: 'average',
                                    name: '平均值'
                                }]
                            }
                        }, {
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
                            },
                            itemStyle: {
                                normal: {
                                    color: '#ff8c00'
                                }
                            },
                            markLine: {
                                data: [{
                                    type: 'average',
                                    name: '平均值'
                                }]
                            }
                        }, {
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
                            },
                            markLine: {
                                data: [{
                                    type: 'average',
                                    name: '平均值'
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
                option.series[1].data = data.series[1].data;
                option.series[1].name = data.series[1].name;
                option.series[1].type = data.series[1].type;
                option.series[2].data = data.series[2].data;
                option.series[2].name = data.series[2].name;
                option.series[2].type = data.series[2].type;
                chartBottom.hideLoading();

                chartBottom.setOption(option);
                
            }
            function onLoadError(error) {
                AlertService.error(error.data.message);
            }
        }
        loadFinishRateChart();
        function loadFinishRateChart () {
        	if(pagingParams.fromDate == undefined){
        		pagingParams.fromDate = "";
        	};
        	if(pagingParams.toDate == undefined){
        		pagingParams.toDate = "";
        	};
        	if(pagingParams.id == undefined){
        		pagingParams.id = "";
        	};
        	ProjectWeeklyStatChart.queryFinishRateChart({
                fromDate : pagingParams.fromDate,
                toDate : pagingParams.toDate,
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
                        y: '30'
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
                var chartBottom = echarts.init(document.getElementById('tabcontent2'));
                option.legend.data = data.legend;
                option.title.text = data.title;
                option.xAxis[0].data = data.category;
                option.series[0].data = data.series[0].data;
                option.series[0].name = data.series[0].name;
                option.series[0].type = data.series[0].type;
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
        vm.backDetail = backDetail;
        function backDetail(){
        	$state.go('project-weekly-stat-detail', {
        		id: pagingParams.id
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
