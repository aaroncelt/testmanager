/*
 * Highcharts drawing functions for TestManager.
 *
 * @author Istvan_Pamer
 */

function createLineChart(renderToID, chartTitle, yAxisTitle, categories, datas) {
	var chart;
	chart = new Highcharts.Chart({
        chart: {
            renderTo: renderToID,
            defaultSeriesType: 'line',
            marginRight: 130,
            marginBottom: 25
        },
        title: {
            text: chartTitle,
            x: -20 //center
        },
        xAxis: {
            categories: categories
        },
        yAxis: {
            title: {
                text: yAxisTitle
            },
            plotLines: [{
                value: 0,
                width: 1,
                color: '#808080'
            }]
        },
        tooltip: {
            formatter: function() {
                    return '<b>'+ this.series.name +'</b><br/>'+
                    this.x +': '+ this.y;
            }
        },
        legend: {
            layout: 'vertical',
            align: 'right',
            verticalAlign: 'top',
            x: -10,
            y: 100,
            borderWidth: 0
        },
        series: datas
    });
}

function createPieChart(renderToID, chartTitle, datas) {
    var chart;
    chart = new Highcharts.Chart({
        chart: {
            renderTo: renderToID,
            plotBackgroundColor: null,
            plotBorderWidth: null,
            plotShadow: false
        },
        title: {
            text: chartTitle
        },
        tooltip: {
            formatter: function() {
                return '<b>'+ this.point.name +'</b>: '+ Math.round(this.percentage) +' %';
            }
        },
        plotOptions: {
            pie: {
                allowPointSelect: true,
                cursor: 'pointer',
                dataLabels: {
                    enabled: true,
                    color: '#000000',
                    connectorColor: '#000000',
                    formatter: function() {
                        return '<b>'+ this.point.name +'</b>: '+ this.point.y;
                    }
                }
            }
        },
        series: [{
            type: 'pie',
            name: '',
            data: datas
        }]
    });
}
