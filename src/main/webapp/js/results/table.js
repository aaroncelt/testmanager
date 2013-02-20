$(document).ready(function() {
    // Report charts show/hide
    $('div.report').hide();
    $('div#tools #charts').click(function() {
        $('div.report').toggle();
    });

    $('img').each(function() {
        var testName = $(this).attr('testname');
        var paramName = $(this).attr('paramname');
        var index = $(this).attr('index');
        $(this).toggle(function() {
            $('#progressbar').toggle();
            $.get("checkpoints", {
                setId : setId,
                testName : testName,
                paramName : paramName
            }, function(data) {
                $('.checkpoints_for_row_' + index).html(data);
                $('#progressbar').toggle();
                $('.checkpoints_for_row_' + index).toggle();
            });
        }, function() {
            $('.checkpoints_for_row_' + index).toggle();
        });

    });

    createPieChart('chart-main', '', datas);
    createPieChart('chart-cp', '', cpDatas);
});

function saveAll() {
    $('#saveAllLink').hide();
    $('#saveAllLinkHidden').show();
    $.get("save_all", {
        setId : setId
    }, function(data) {
        $('#saveAllLinkHidden').hide();
        $('#saveAllLink').show();
        window.location.reload();
    });
}

function saveSelected(testName, paramName, index) {
    $('#saveLink-' + index).hide();
    $('#saveLinkHidden-' + index).show();
    $.get("save", {
        setId : setId,
        testName : testName,
        paramName : paramName,
        type : $('#errorType-' + index).val(),
        comment : $('#comment-' + index).val()
    }, function(data) {
        $('#saveLinkHidden-' + index).hide();
        $('#saveLink-' + index).show();
        window.location.reload();
    });
}

function toggleCp(index) {
    $('.cp-' + index).toggle();
}
