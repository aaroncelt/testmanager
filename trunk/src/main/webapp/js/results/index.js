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
                $.cookie("selected-sets", toShow.join("|"));
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

            // Environment table cell is shortened, show full content in a popup
            // if the cell is clicked
            $('.set-results-box').each(function() {
                $(this).find('td.env').click(
                // set position near the mouse cursor and set its content from
                // the table cell
                function(e) {
                    var popup = $('.env-popup');
                    popup.show().find('#env-popup-content').html($(this).text());
                    popup.css('top', e.pageY);
                    popup.css('left', e.pageX + 50);
                });
            });

            // Environment popup window close button
            $('.env-popup .close-button').click(function() {
                $('.env-popup').hide();
            });
        });

function deleteSelected(setId) {
    var r = confirm("Do you really want to delete the following set? \r\n\n " + setId);
    if (r == true) {
        window.location.href = "delete?setId=" + setId;
    }
}
