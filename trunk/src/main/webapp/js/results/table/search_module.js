var hiddenRows = false;
var cache = [];
var filteredIndex = [];

// Functions
$(document).ready(function() {
    $('#search-module span.search-icon').click(function(){
       filter(); 
    });
    $('#search-module span.clear-icon').click(function(){
        $('#search-text').val('');
        $('#found-item-message').hide();
        showAllRows();
     });
    // Search text box keypress event
    $('#search-text').keyup(function(event) {
        if($(this).val().length < 3){
            $('#found-item-message').hide();
            showAllRows();
            return
        }
        if (event.which == 13){
            filter();
        } else {
            search($(this).val());
        }
    });
    $('table#main').find('tr.tableRow').each(function(i,row){
        cache.push(getRowAsString(row));
    });
});

function showAllRows(){
    if (hiddenRows){
        $('table#main tr.tableRow').show();
        hiddenRows = false;
    }
}

function hideAllRows(){
    $('table#main tr.tableRow').hide();
    hiddenRows = true;
}

function search(inputVal){
    filteredIndex = [];
    $.each(cache, function(index, row){
        var regExp = new RegExp(inputVal, 'i');
        if(regExp.test(row)){
            filteredIndex.push(index);
        }
    });
    $('#found-item-count').text(filteredIndex.length);
    $('#found-item-message').show();
}

function filter(){
    if (filteredIndex.length == 0){
        return
    }
    hiddenRows = true;
    hideAllRows();
    var allRows = $('table#main tr.tableRow');
    $.each(filteredIndex, function(i,item){
        $(allRows[item]).show();
    });
}

function getRowAsString(row){
    return $(row).find('td').map(function(i, td){
        if ($(td).find('option:selected').length > 0){
            return $(td).find('option:selected').text().trim();
        }
        return $(td).text().trim();
        }).toArray().join(" ");
}