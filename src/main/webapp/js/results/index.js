$(document).ready(function() {
	$('div#filterForm').toggle();
	$('div#filterFormText').click(
			function() {
				$('div#filterForm').toggle(1000);
			});
});

function deleteSelected(setId) {
    var r = confirm("Are you sure?");
    if (r == true) {
        window.location.href = "delete?setId=" + setId;
    }
}

function filterSelected(filterLink) {
    var temp = $('#' + filterLink).attr('href') + "?filterKey=" + $('#headFilterSelect').val() + "&filterValue=" + $('#headFilterArea').val();
    $('#' + filterLink).attr('href', temp);
}