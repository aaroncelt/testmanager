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

    // Bulk save popdiv behaviour
    $("#bulk-save-popdiv").draggable();
    $("#bulk-comment").click(function() {
        if ($("#bulk-save-popdiv").is('.ui-draggable-dragging')) {
            return;
        }
        $("#bulk-save-popdiv").draggable("option", "disabled", true);
    }).focusout(function() {
        $("#bulk-save-popdiv").draggable('option', 'disabled', false);
    });
    $("#bulk-save-popdiv .close-button").click(function() {
        $("#bulk-save-popdiv").hide();
        $(".saveLink").removeClass("save-link-disabled");
    });

    $(".bulk-select").change(function() {
        if ($(".bulk-select:checked").length > 0) {
            $("#bulk-save-popdiv").show();
            $(".saveLink").addClass("save-link-disabled");
        } else {
            $("#bulk-save-popdiv").hide();
            $(".saveLink").removeClass("save-link-disabled");
        }
    });
    
    $(".select-all").change(function() {
		if ($(this).is(":checked")){
			$(".bulk-select:visible").each(function() {
				$(this).attr('checked', 'checked');
			});
			$("#bulk-save-popdiv").show();
			$(".saveLink").addClass("save-link-disabled");
		} else {
			$(".bulk-select:visible").each(function() {
				$(this).removeAttr('checked');
			});
			$("#bulk-save-popdiv").hide();
			$(".saveLink").removeClass("save-link-disabled");
		}
	});

    $("#bulk-save-button").click(function() {
        bulkSave();
    });
    
    $("#show-hide-labels").click(function() {
    	$("div.labels").toggle();
    });
    createPieChart('chart-main', '', datas);
    createPieChart('chart-cp', '', cpDatas);
    
    $('div#cp-filter ul.checkbox input[type=checkbox]').change(function() {

	    var vals = $('div#cp-filter ul.checkbox input[type=checkbox]:checked').map(function() {
	        return $(this).val();
	    }).get().join('|');

	    $('input#filterCpLabels').val(vals);

	});
    
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
function bulkSave() {
    $.ajaxSetup({async:false});
    $("input.bulk-select:checked").each(function() {
        $("#progressbar").show();
        var line = $(this).closest("tr");
        var testName = $(line).find("td.testName").data("test-name");
        var paramName = $(line).find("td.paramName").data("param-name");
        var errorType = $("#bulk-error-type").val();
        var comment = $("#bulk-comment").text();
        save(setId, testName, paramName, errorType, comment, null);
    });
    $.ajaxSetup({async:true});
    window.location.reload();
}
function saveLine(line) {
    var testName = $(line).find("td.testName").data("test-name");
    var paramName = $(line).find("td.paramName").data("param-name");
    var errorType = $(line).find("select.error-type").val();
    var comment = $(line).find(".comment").val();

    console.log("Saving: " + setId + " - " + testName + " - " + paramName
            + " - " + errorType + " - " + comment);
    $(line).find('.saveLink').hide();
    $(line).find('.saveLinkHidden').show();
    save(setId, testName, paramName, errorType, comment, function() {
        $(line).find('.saveLinkHidden').hide();
        $(line).find('.saveLink').show();
        window.location.reload();
    });
}

function save(setId, testName, paramName, errorType, comment, callback) {
    $.get("save", {
        setId : setId,
        testName : testName,
        paramName : paramName,
        type : errorType,
        comment : comment
    }, function(data) {
        if (typeof (callback) === "function") {
            callback();
        }
    });
    
}

function toggleCp(index) {
    $('.cp-' + index).toggle();
}
