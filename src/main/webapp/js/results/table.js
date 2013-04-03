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
            $('#progressbar').show();
            $.get("checkpoints", {
                setId : setId,
                testName : testName,
                paramName : paramName
            }, function(data) {
                $('.checkpoints_for_row_' + index).html(data);
                $('#progressbar').hide();
                $('.checkpoints_for_row_' + index).show();
            });
        }, function() {
            $('.checkpoints_for_row_' + index).hide();
        });

    });

    $('div[contentEditable=true]').each(function() {
        var content = $(this).text();
        $(this).focusin(function() {
            $(this).addClass("edit-in-progress");
        });
        $(this).focusout(function() {
            $(this).removeClass("edit-in-progress");
            if (content != $(this).text()) {
                $(this).addClass("changed");
            } else {
                $(this).removeClass("changed");
            }
        });
    });

    $("span.saveLink").each(function() {
        $(this).click(function() {
            saveLine($(this).closest("tr"));
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

function saveLine(line) {
    var testName = $(line).find("td.testName").data("test-name");
    var paramName = $(line).find("td.paramName").data("param-name");
    var errorType = $(line).find("select.error-type").val();
    var comment = $(line).find(".comment").text();

    console.log("Saving: " + setId + " - " + testName + " - " + paramName
            + " - " + errorType + " - " + comment);
    $(line).find('.saveLink').hide();
    $(line).find('.saveLinkHidden').show();
    $.get("save", {
        setId : setId,
        testName : testName,
        paramName : paramName,
        type : errorType,
        comment : comment
    }, function(data) {
        $(line).find('.saveLinkHidden').hide();
        $(line).find('.saveLink').show();
        window.location.reload();
    });
}

function toggleCp(index) {
    $('.cp-' + index).toggle();
}
