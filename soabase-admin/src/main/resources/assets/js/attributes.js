function soaAttributesAddUpdate() {
    var key = $('#soa-attributes-new-key').val().trim();
    var scope = $('#soa-attributes-new-scope').val().trim();
    var value = $('#soa-attributes-new-value').val().trim();

    if ( key.length === 0 ) {
        bootbox.alert("Attribute key cannot be empty");
        return;
    }

    var attribute = {
        "key": key,
        "scope": scope,
        "value": value
    };
    soaShowInfiniteProgressBar();
    $.ajax({
        type: "POST",
        url: '/soa/attributes',
        contentType: "application/json",
        data: JSON.stringify(attribute),
        processData: false,
        success: function() {
            soaHideInfiniteProgressBar();
            soaSetAttributeRows();
        },
        error: function(jqXHR, textStatus, errorThrown) {
            soaHideInfiniteProgressBar();
            bootbox.alert('Operation failed: ' + errorThrown);
        }
    });
}

function soaAttributesDelete(attribute) {
    soaShowInfiniteProgressBar();
    $.ajax({
        type: "DELETE",
        url: '/soa/attributes',
        contentType: "application/json",
        data: JSON.stringify(attribute),
        processData: false,
        success: function() {
            soaHideInfiniteProgressBar();
            soaSetAttributeRows();
        },
        error: function(jqXHR, textStatus, errorThrown) {
            soaHideInfiniteProgressBar();
            bootbox.alert('Operation failed: ' + errorThrown);
        }
    });
}

function soaAttributesGetDeleteClickFunction(attribute) {
    return function(){
        var str = attribute.key;
        if ( attribute.scope.length > 0 ) {
            str = attribute.scope + '/' + str;
        }
        bootbox.confirm('Are you sure you want to delete "' + str + '"?', function(r){
            if ( r ) {
                soaAttributesDelete(attribute);
            }
        });
    }}

var soaAttributesData = [];
var soaAttributesSortType = 'asc';
var soaAttributesSortColumn = -1;

function soaAttributesSort(columnIndex) {
    if ( columnIndex === soaAttributesSortColumn ) {
        soaAttributesSortType = (soaAttributesSortType === 'asc') ? 'desc' : 'asc';
    } else {
        soaAttributesSortType = 'asc';
    }
    soaAttributesSortColumn = columnIndex;

    soaAttributesData.sort(function(a, b){
        var keyDiff = a.key.localeCompare(b.key);
        var scopeDiff = a.scope.localeCompare(b.scope);
        var valueDiff = a.value.localeCompare(b.value);
        if ( soaAttributesSortType === 'desc' ) {
            keyDiff = -1 * keyDiff;
            scopeDiff = -1 * scopeDiff;
            valueDiff = -1 * valueDiff;
        }

        if ( soaAttributesSortColumn == 0 ) {
            return keyDiff ? keyDiff : (valueDiff ? valueDiff : scopeDiff);
        }

        if ( soaAttributesSortColumn == 1 ) {
            return scopeDiff ? scopeDiff : (keyDiff ? keyDiff : valueDiff);
        }

        return valueDiff ? valueDiff : (scopeDiff ? scopeDiff : keyDiff);
    });

    var rows = "";
    for (var i in soaAttributesData) {
        var attribute = soaAttributesData[i];
        rows = rows + '<tr>';
        rows = rows + '<td>' + attribute.key + "</td>";
        rows = rows + '<td>' + attribute.scope + "</td>";
        rows = rows + '<td>' + attribute.value + "</td>";
        rows = rows + '<td class="soa-attributes-delete-button-cell"><button id="soa-attributes-delete-button-' + i + '" class="btn btn-xs">Delete...</button></td>';
        rows = rows + '</tr>';
    }
    $('#soa-attributes-rows').html(rows);
    for (i in soaAttributesData) {
        var attribute = soaAttributesData[i];
        $('#soa-attributes-delete-button-' + i).click(soaAttributesGetDeleteClickFunction(attribute));
    }
}

function soaSetAttributeRows() {
    soaAttributesSortColumn = -1;
    $.getJSON('/soa/attributes/all', function(data){
        soaAttributesData = data;
        soaAttributesSort(0);
    });
}

$(function() {
    soaSetAttributeRows();
    $('#soa-attributes-add-button').click(function(){
        soaAttributesAddUpdate();
    });
    $('#soa-attributes-header-attribute').click(function(){
        soaAttributesSort(0);
    });
    $('#soa-attributes-header-scope').click(function(){
        soaAttributesSort(1);
    });
    $('#soa-attributes-header-value').click(function(){
        soaAttributesSort(2);
    });
    $('#soa-attributes-refresh').click(function(){
        soaSetAttributeRows();
    });
    $('#soa-attributes-collapse-current').click(function(){
        $(this).toggleClass('glyphicon-expand glyphicon-collapse-down');
        $('#soa-attributes-current').collapse('toggle');
        return true;
    });
    $('#soa-attributes-current').on('hide.bs.collapse', function(){
        $('#soa-attributes-refresh').hide();
    });
    $('#soa-attributes-current').on('show.bs.collapse', function(){
        $('#soa-attributes-refresh').show();
    });
});
