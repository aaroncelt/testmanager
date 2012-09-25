$(document).ready(function() {
	$('#filterForm').toggle();
	$('#setList').toggle();

	$('#filterFormText').click(function() {
		$('#filterForm').toggle(1000);
	});

	$('#setListText').click(function() {
		$('#setList').toggle(1000);
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
