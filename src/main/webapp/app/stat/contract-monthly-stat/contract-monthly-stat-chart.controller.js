(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ContractMonthlyStatChartController', ContractMonthlyStatChartController);

    ContractMonthlyStatChartController.$inject = ['$scope','$state','DateUtils', '$rootScope', '$stateParams', 'pagingParams', 'ContractMonthlyStatChart', 'AlertService','previousState'];

    function ContractMonthlyStatChartController ($scope,$state,DateUtils,$rootScope, $stateParams, pagingParams, ContractMonthlyStatChart, AlertService,previousState) {
    	var vm = this;
//    	console.log($state);
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
        vm.searchQuery.contractId = pagingParams.contractId;
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
        	if(pagingParams.contractId == undefined){
        		pagingParams.contractId = "";
        	};
        	ContractMonthlyStatChart.queryChart({
                fromDate : pagingParams.fromDate,
                toDate : pagingParams.toDate,
                contractId : pagingParams.contractId
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
                contractId:vm.searchQuery.contractId ? vm.searchQuery.contractId.key : ""
            });
        }
        vm.backDetail = backDetail;
        function backDetail(){
        	$state.go('contract-monthly-stat-detail', {
        		id: pagingParams.contractId
            });
        }
        
        function clear() {
            vm.searchQuery = {};
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
