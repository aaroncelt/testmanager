$(document).ready(function() {
	$('#filterForm').toggle();
	$('#setList').toggle();

	$('#filterFormText').click(function() {
		$('#filterForm').toggle();
	});

	$('#setListText').click(function() {
		$('#setList').toggle();
	});

	$('button.filterLink').each(function(i, element) {
		$(element).click(function() {
			 var tmp = $(this).attr('filterType')
											+ "?filterKey="
											+ $('#headFilterSelect').val()
											+ "&filterValue="
											+ $('#headFilterArea').val();
									window.location.href = tmp;

		});
	});

});

function deleteSelected(setId) {
	var r = confirm("Are you sure?");
	if (r == true) {
		window.location.href = "delete?setId=" + setId;
	}
}
