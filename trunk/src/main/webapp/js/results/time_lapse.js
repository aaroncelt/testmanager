$(document).ready(function() {
    createLineChart('chart-pass', '', 'Pass Rate (%)', passChartCategories, passChartData);
    createLineChart('chart-fail', '', 'Issue Number', failChartCategories, failChartData);
});