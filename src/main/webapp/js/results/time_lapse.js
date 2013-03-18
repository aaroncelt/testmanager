$(document).ready(function() {
    var timeLapseOrder = $.cookie('timeLapseOrder');
    if (timeLapseOrder == null || "asc" == timeLapseOrder){
        $.cookie('timeLapseOrder',"asc");
        timeLapseOrder = "Oldest first";
    } else if ("desc" == timeLapseOrder) {
        timeLapseOrder = "Newest first";
    }
    $('#time-lapse-order span').text(timeLapseOrder);
    
    $('#time-lapse-order span').click(function (){
        if ("desc" == $.cookie('timeLapseOrder')){
            $.cookie('timeLapseOrder',"asc");
        } else{
            $.cookie('timeLapseOrder',"desc");
        }
        location.reload();
    });
    
    createLineChart('chart-pass', '', 'Pass Rate (%)', passChartCategories, passChartData);
    createLineChart('chart-fail', '', 'Issue Number', failChartCategories, failChartData);
});