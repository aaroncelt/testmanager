function showAllRows() {
	$('table.main tbody tr').show();
}

function filter() {
	var term = terms = $("div#labels-selected select option").map(function() {
		return $(this).val();
	}).get().join(",");

	var searchMethod = $("input[name=search-method]:checked").val();
	if (term == '') {
		showAllRows();
	} else {
		$('table.main tbody tr').each(
				function() {
					var currentItem = $(this).find("td:first div.labels")
							.text().replace(/[\[\]]/g, '');
					var matched;
					if ('all' == searchMethod) {
						matched = containsAll(term.split(","), currentItem
								.split(', '));
					} else if ('any' == searchMethod) {
						matched = containsAny(term.split(","), currentItem
								.split(', '));
					} else {
						matched = containsAll(term.toLowerCase().split(","),
								currentItem.toLowerCase());
					}

					if (matched) {
						$(this).show();
					} else {
						$(this).hide();
					};
				});
	}
}

function filter_text(term) {
	$('table.main tbody tr').each(
			function() {
				var currentItem = $(this).find("td:first").text();
				matched = containsAll(term.toLowerCase().split(","),
						currentItem.toLowerCase());
				if (matched) {
					$(this).show();
				} else {
					$(this).hide();
				};
			});
}

function containsAll(terms, items) {
	for ( var i = 0; i < terms.length; i++) {
		if (items.indexOf(terms[i]) === -1) {
			return false;
		}
	}
	return true;
}

function containsAny(terms, items) {
	for ( var i = 0; i < terms.length; i++) {
		if (items.indexOf(terms[i]) > -1) {
			return true;
		}
	}
	return false;
}

function showHidePhases() {
	if ($('.hidden-phases').length > 0) {
		$('.hidden-phases').removeClass('hidden-phases').addClass(
				'visible-phases');
	} else {
		$('.visible-phases').removeClass('visible-phases').addClass(
				'hidden-phases');
	}
}

function removeLabel(label) {
	if (label.length > 0) {
		$("div#labels-available select").append($(label).clone());
		$(label).remove();
	}
	filter();
}

function addLabel(label) {
	if (label.length > 0) {
		$("div#labels-selected select").append($(label).clone());
		$(label).remove();
	}
	filter();
}

function searchMethodChange() {
	filter();
}

$(document).ready(function() {
	$('td.cp-group').hover(function() {
		var t = parseInt($(this).index()) + 1;
		$('td:nth-child(' + t + ')').addClass('highlighted');
	}, function() {
		var t = parseInt($(this).index()) + 1;
		$('td:nth-child(' + t + ')').removeClass('highlighted');
	});

	$('#search-input').keyup(function(event) {
		if (event.which == 13) {
			filter_text($(this).val());
		}
	});

	$("#label-search button.add-label").click(function() {
		addLabel($("#label-search select.sel-labels option:selected"));
	});

	$("#label-search button.remove-label").click(function() {
		removeLabel($("#label-search select.sel-labels option:selected"));
	});

	$("#label-search button.remove-all-label").click(function() {
		removeLabel($("#label-search select.sel-labels option"));
	});

	$("#label-search input[name=search-method]:radio").change(function() {
		searchMethodChange();
	});

	$("div.headingDiv h3").click(function() {
		$("div.info-container>div").slideToggle(500);
	});
	$("div.info-container>div").toggle();

	$("div.labels").show();
});