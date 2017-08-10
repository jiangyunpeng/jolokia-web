angular.module("myApp").component('heapChart', {
    templateUrl: '/static/component/charts/simpleChart.html',
    bindings: {
    },
    controllerAs: 'ctx',
    controller: function($rootScope, DashboardService, UtilService){
        this.labels = DashboardService.heapChartData().labels;
        this.series = DashboardService.heapChartData().series;
        this.data = DashboardService.heapChartData().data;

        this.datasetOverride = [{ yAxisID: 'y-axis-heap1' }];
        this.options = {
            scales: {
                yAxes: [{
                    id: 'y-axis-heap1',
                    type: 'linear',
                    display: true,
                    position: 'left',
                    ticks: {
                        beginAtZero:true,
                        callback: function(label, index, labels) {
                            return UtilService.formatBytes(label, 0);
                        }
                    },
                    scaleLabel:{
                        display: true,
                        labelString: 'Memory usage',
                        fontColor: "#666666"
                    }
                }]
            },
            tooltips: {
                enabled: true,
                mode: 'label',
                callbacks: {
                    label: function (tooltipItems, data) {
                        return data.datasets[tooltipItems.datasetIndex].label + ': ' + UtilService.formatBytes(tooltipItems.yLabel, 0);
                    }
                }
            }
        };

        this.onClick = function (points, evt) {
            console.log(points, evt);
        };

        $rootScope.$on('chartChange',function(){
            this.labels = DashboardService.heapChartData().labels;
            this.series = DashboardService.heapChartData().series;
            this.data = DashboardService.heapChartData().data;
        });
    }
});