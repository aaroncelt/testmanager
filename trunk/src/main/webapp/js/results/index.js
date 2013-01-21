$(document).ready(
        function() {
            $('#filterForm').toggle();
            $('#setList').toggle();

            $('#filterFormText').click(function() {
                $('#setList').hide();
                $('#filterForm').toggle();
            });

            $('#setListText').click(function() {
                $('#setList').toggle();
                $('#filterForm').hide();
            });

            $('button.filterLink').each(
                    function(i, element) {
                        $(element).click(
                                function() {
                                    var tmp = $(this).attr('filterType')
                                    + "?filterKey="
                                    + $('#headFilterSelect').val()
                                    + "&filterValue="
                                    + $('#headFilterArea').val();
                                    window.location.href = tmp;
                                });
                    });

            $('#selectedSet').change(function() {
                var toShow = [];
                $("#selectedSet option:selected").each(function() {
                    toShow.push($(this).val());
                });
                console.log(toShow);
                $.cookie("selected-sets",toShow.join("|"));
                if ("all" === $("#selectedSet option:selected").val()) {
                    $('.set-results-box').each(function() {
                        $(this).show();
                    });
                } else {
                    $('.set-results-box').each(function() {
                        console.log($(this).attr('id'));
                        if ($.inArray($(this).attr('id'), toShow) === -1) {
                            $(this).hide();
                        } else {
                            $(this).show();
                        }
                    });
                }
            });
        });

function deleteSelected(setId) {
    var r = confirm("Are you sure?");
    if (r == true) {
        window.location.href = "delete?setId=" + setId;
    }
}
