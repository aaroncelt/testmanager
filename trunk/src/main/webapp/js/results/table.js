$(document).ready(
		function() {

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

			// Filter type change
			$('#filterType select').change(function() {
				var type = $('#filterType select option:selected').val();
				if ("errorType" === type) {
					$('#filterValue #text').hide();
					$('#filterValue #checkBox').hide();
					$('#filterValue #errorTypes').show();
					$('#filterValue #resultStates').hide();
				} else if ("isAnalyzed" === type) {
					$('#filterValue #text').hide();
					$('#filterValue #checkBox').show();
					$('#filterValue #errorTypes').hide();
					$('#filterValue #resultStates').hide();
				} else if ("resultState" === type) {
					$('#filterValue #text').hide();
					$('#filterValue #checkBox').hide();
					$('#filterValue #errorTypes').hide();
					$('#filterValue #resultStates').show();
				} else {
					$('#filterValue #text').show();
					$('#filterValue #checkBox').hide();
					$('#filterValue #errorTypes').hide();
					$('#filterValue #resultStates').hide();
				}
			});

			// Filter button click
			$('#filterButton').click(
					function() {
						var type = $('#filterType option:selected').val();
						var filterText = $('#filterValue #text').val();
						if ("errorType" === type) {
							filterText = $('#filterValue #errorTypes').find(
									'option:selected').val();
						} else if ("resultState" === type) {
							filterText = $('#filterValue #resultStates').find(
									'option:selected').val();
						} else if ("isAnalyzed" === type) {
							filterByAnalyzed($('#filterValue #checkBox').is(
									":checked"));
							return;
						}
						filterResult(type, filterText);
					});

			// Clear button click
			$('#filterClear').click(function() {
				$('tr.tableRow').each(function() {
					$(this).show();
				});
			});

			// Filter, Search show/hide
			$('div.filterModule').toggle();
			$('div#tools #filter').click(function() {
				$('div.filterModule').toggle(1000);
			});
			$('div#tools #charts').click(function() {
				$('div.report').toggle(1000);
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

function filterResult(filterType, filterText) {
	$('tr.tableRow').each(
			function() {
				var fieldValue = $(this).find('.' + filterType).text();
				if ("errorType" === filterType) {
					fieldValue = $(this).find(
							'.' + filterType + ' option:selected').text();
				}
				console.log(filterType + " - " + filterText + " - "
						+ fieldValue);
				if (fieldValue.indexOf(filterText) == -1) {
					console.log("hide");
					$(this).hide();
				} else {
					$(this).show();
				}
			});
}

function filterByAnalyzed(isAnalyzed) {
	$('tr.tableRow').each(function() {
		var analyzed = $(this).attr('class').indexOf("commentless") == -1;
		if ((isAnalyzed && analyzed) || (!isAnalyzed && !analyzed)) {
			$(this).show();
		} else {
			$(this).hide();
		}
	});

}