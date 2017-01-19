(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ContractMonthlyStatChartController', ContractMonthlyStatChartController);

    ContractMonthlyStatChartController.$inject = ['$scope','$state','DateUtils', '$rootScope', '$stateParams', 'pagingParams', 'ContractMonthlyStatChart', 'AlertService','previousState'];

    function ContractMonthlyStatChartController ($scope,$state,DateUtils,$rootScope, $stateParams, pagingParams, ContractMonthlyStatChart, AlertService,previousState) {
    	var vm = this;
        vm.transition = transition;
        vm.clear = clear;
        vm.loadAll = loadAll;
        vm.search = search;
        vm.searchQuery = {};
        var fromDate = pagingParams.fromDate;
        var toDate = pagingParams.toDate;
        if(fromDate && fromDate.length == 6){
        	fromDate = new Date(fromDate.substring(0,4),parseInt(fromDate.substring(4,6)),fromDate.substring(6,8));
        }
        if(toDate && toDate.length == 6){
        	toDate = new Date(toDate.substring(0,4),parseInt(toDate.substring(4,6)),toDate.substring(6,8));
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
        	ContractMonthlyStatChart.queryChart({
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
                        data: []
                    },
                    toolbox: {
                        show: true,
                        feature: {
                            mark: {
                                show: true
                            },
                            dataZoom: {
                                yAxisIndex: 'none'
                            },
                            dataView: {
                                show: true,
                                readOnly: false
                            },
                            magicType: {
                                show: true,
                                type: ['line', 'bar']
                            },
                            
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
                        {//1
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
                        }, {//2
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
                        }, {//3
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
                        }, {//4
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
                        }, {//5
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
                        }, {//6
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
                        }, {//7
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
                        }, {//8
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
                        }, {//9
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
                        }, {//10
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
                        }, {//11
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
                        }, {//12
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
                chartBottom.hideLoading();

                chartBottom.setOption(option);
                
            }
            function onError(error) {
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
        	ContractMonthlyStatChart.queryFinishRateChart({
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
                        data: []
                    },
                    toolbox: {
                        show: true,
                        feature: {
                            mark: {
                                show: true
                            },
                            dataZoom: {
                                yAxisIndex: 'none'
                            },
                            dataView: {
                                show: true,
                                readOnly: false
                            },
                            magicType: {
                                show: true,
                                type: ['line', 'bar']
                            },
                            restore: {
                                show: true
                            },
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
                fromDate: DateUtils.convertLocalDateToFormat(vm.searchQuery.fromDate,"yyyyMM"),
                toDate: DateUtils.convertLocalDateToFormat(vm.searchQuery.toDate,"yyyyMM"),
                id:vm.searchQuery.id ? vm.searchQuery.id : ""
            });
        }
        vm.backDetail = backDetail;
        function backDetail(){
        	$state.go('contract-monthly-stat-detail', {
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
        	if (!vm.searchQuery.workDay && !vm.searchQuery.toDate){
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
