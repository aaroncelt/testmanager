var hiddenRows = false;
var headers = [];
var cache = [];
var filteredIndex = [];

// Functions
$(document).ready(function() {
    var searchInFromCookie = $.cookie('search-in');
    if (searchInFromCookie != null){
        $('#search-module div#search-in span[data-column=' + $.cookie('search-in')+']').addClass("selected");
    } else {
        $('#search-module div#search-in span[data-column=-1]').addClass("selected");
    }
    
    
    $('#search-module span.search-icon').click(function(){
       filter(); 
    });
    
    $('#search-module span.clear-icon').click(function(){
        $('#search-text').val('');
        $('#found-item-message').hide();
        showAllRows();
     });
    
    $('#search-module div#search-in span').click(function(){
        $('#search-module div#search-in span').removeClass("selected");
        $(this).addClass("selected");
        $('#search-text').keyup();
        $.cookie('search-in', $('#search-module div#search-in span.selected').data('column'));
    });
    
    // Search text box keypress event
    $('#search-text').keyup(function(event) {
        if($(this).val().length < 3){
            $('#found-item-message').hide();
            showAllRows();
            return
        }
        var searchIn = $('#search-module div#search-in span.selected').data('column');
        if (event.which == 13){
            filter();
        } else {
            search(searchIn, $(this).val());
        }
    });
    
    createCache();
});

function showAllRows(){
    if (hiddenRows){
        $('table.main tr.table-row').show();
        hiddenRows = false;
    }
}

function hideAllRows(){
    $('table.main tr.table-row').hide();
    hiddenRows = true;
}

function search(column, inputVal){
    filteredIndex = [];
    $.each(cache, function(index, row){
        var regExp = new RegExp(inputVal, 'i');
        var rowData;
        if(column == -1){
            rowData = row.toString();
        } else {
            rowData = row[column];
        }
        if(regExp.test(rowData)){
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
    var allRows = $('table.main tr.table-row');
    $.each(filteredIndex, function(i,item){
        $(allRows[item]).show();
    });
}

function getRow(row){
    return getRow(row, 'td');
}

function getRow(cells){
    return $(cells).map(function(i, td){
        if ($(td).find('option:selected').length > 0){
            return $(td).find('option:selected').text().trim();
        }
        return $(td).text().trim();
        }).toArray();
}

function createCache(){
    $('table.main').find('tr.table-row').each(function(i,row){
        cache.push(getRow($(row).find('td')));
    });
    $('table.main').find('tr.header-row').each(function(i,row){
        headers.push(getRow($(row).find('th')));
    });
}