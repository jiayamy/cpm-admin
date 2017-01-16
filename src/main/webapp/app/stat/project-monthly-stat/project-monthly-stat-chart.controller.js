(function() {
    'use strict';

    angular
        .module('cpmApp')
        .controller('ProjectMonthlyStatChartController', ProjectMonthlyStatChartController);

    ProjectMonthlyStatChartController.$inject = ['$scope', '$rootScope', '$stateParams', 'pagingParams', 'ProjectMonthlyStatChart', 'AlertService'];

    function ProjectMonthlyStatChartController ($scope, $rootScope, $stateParams, pagingParams, ProjectMonthlyStatChart, AlertService) {
    	var vm = this;

        vm.transition = transition;
        vm.clear = clear;
        vm.loadAll = loadAll;
        vm.search = search;
        vm.searchQuery = {};
//        vm.projectInfos = [];
//        if (!vm.searchQuery.projectId){
//        	vm.haveSearch = null;
//        }else{
//        	vm.haveSearch = true;
//        }
        console.log($stateParams.id);
        console.log(pagingParams.projectId);
        loadAll();

        function loadAll () {
        	if(pagingParams.fromDate == undefined){
        		pagingParams.fromDate = "";
        	};
        	if(pagingParams.toDate == undefined){
        		pagingParams.toDate = "";
        	};
        	if(pagingParams.projectId == undefined){
        		pagingParams.projectId = "";
        	};
        	ProjectMonthlyStatChart.queryChart({
                fromDate : pagingParams.fromDate,
                toDate : pagingParams.toDate,
                projectId : pagingParams.projectId
            }, onSuccess, onError);
           function onSuccess(data, headers) {
                console.log(data);
                //第三个参数设置弹出框的显示时间
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
                page: vm.page,
                sort: vm.predicate + ',' + (vm.reverse ? 'asc' : 'desc'),
                projectId:vm.searchQuery.projectId ? vm.searchQuery.projectId.key : ""
            });
        }

        
        function clear() {
            vm.links = null;
            vm.page = 1;
            vm.predicate = 'm.id';
            vm.reverse = true;
            vm.searchQuery = {};
            vm.haveSearch = null;
            vm.transition();
        }
        function search(searchQuery) {
        	if (!vm.searchQuery.projectId){
                return vm.clear();
            }
            vm.links = null;
            vm.page = 1;
            vm.predicate = 'm.id';
            vm.reverse = false;
            vm.haveSearch = true;
            vm.transition();
        }
    }
})();
