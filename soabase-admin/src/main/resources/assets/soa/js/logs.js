var logsHost = null;
var logsPort = null;
var logsAdminPort = null;

function logsDisplay(data) {
    var logFiles = "";
    if ( data.length == 0 ) {
        logFiles = soaGetTemplate('soa-no-log-files-template');
    } else {
        for ( var i in data ) {
            var thisData = data[i];
            var url = 'http://' + logsHost + ':' + logsAdminPort + '/api/soa/logging/file/raw/' + thisData.key;
            url = url + "?name=" + encodeURIComponent(thisData.name);
            var template = soaGetTemplate('soa-log-file-template', {
                '$URL$': url,
                '$NAME$': thisData.name
            });
            logFiles = logFiles + template;
        }
    }

    $('#logs-list').html(logFiles);
}

var logLevels = [
    '',
    'Off',
    'Trace',
    'Debug',
    'Error',
    'Warn',
    'Info',
    'All'
];

function logLevelsDisplay(data) {
    var logLevelRows = "";
    for ( var i in data ) {
        var thisData = data[i];
        var options = '';
        for ( var x in logLevels ) {
            var thisLevel = logLevels[x];
            if ( (thisData.name.toUpperCase() == 'ROOT') && (thisLevel == '') ) {
                continue;
            }
            var dataLevelValue = (thisData.level != null) ? thisData.level.toUpperCase() : '';
            var thisSelected = (dataLevelValue.toUpperCase() == thisLevel.toUpperCase()) ? ' SELECTED' : '';
            options = options + '<option' + thisSelected + '>' + thisLevel + "</option>";
        }
        var template = soaGetTemplate('soa-log-level-row-template', {
            'xtr': "tr",
            'xtd': "td",
            '$NAME$': thisData.name,
            '$INDEX$': i,
            '$OPTIONS$': options
        });
        logLevelRows = logLevelRows + template;
    }

    $('#soa-log-level-rows').html(logLevelRows);
}

function logsSubmitResetUi() {
    $('#soa-log-submit-levels-button').removeAttr('disabled');
    $('#soa-progress-container').hide();
}

function logsSubmitLevels() {
    $('#soa-log-submit-levels-button').attr('disabled', 'disabled');
    $('#soa-progress-container').show();

    var data = [];
    for ( var i = 0; true; i = i + 1 ) {
        var selectId = '#soa-log-level-' + i;
        var nameId = '#soa-log-level-name-' + i;
        if ( $(selectId).length && $(nameId).length ) {
            var value = $(selectId).val();
            var item = {
                'name': $(nameId).text(),
                'level': (value == '') ? null : value
            };
            data.push(item);
        } else {
            break;
        }
    }

    url = 'http://' + logsHost + ':' + logsAdminPort + '/api/soa/logging/levels';
    $.ajax({
        type: 'PUT',
        url: url,
        contentType: "application/json",
        data: JSON.stringify(data),
        processData: false,
        success: function() {
            logsSubmitResetUi();
            logsReset();
        },
        error: function(jqXHR, textStatus, errorThrown) {
            logsSubmitResetUi();
            bootbox.alert('Operation failed: ' + errorThrown);
        }
    });
}

function logsReset() {
    var url = 'http://' + logsHost + ':' + logsAdminPort + '/api/soa/logging/files';
    $.getJSON(url, function(data){
        logsDisplay(data);
    });

    url = 'http://' + logsHost + ':' + logsAdminPort + '/api/soa/logging/levels';
    $.getJSON(url, function(data){
        logLevelsDisplay(data);
    });

    $('#soa-logs-last-updated').text('Last updated ' + (new Date()).toLocaleString());
}

function logsInit() {
    $('#logs-host').text(logsHost + ':' + logsPort);

    $('#soa-logs-body-toggle').click(function(){
        $('#soa-logs-body-toggle').toggleClass('glyphicon-expand glyphicon-collapse-down');
        $('#soa-logs-body-collapse').collapse('toggle');
        return true;
    });
    $('#soa-log-levels-body-toggle').click(function(){
        $('#soa-log-levels-body-toggle').toggleClass('glyphicon-expand glyphicon-collapse-down');
        $('#soa-log-levels-body-collapse').collapse('toggle');
        return true;
    });

    $('#soa-attributes-refresh').click(function(){
        logsReset();
        return true;
    });

    $('#soa-log-submit-levels-button').click(function(){
        logsSubmitLevels();
        return true;
    });

    logsReset();
}

$(function(){
    soaAutoLoadTemplates();

    logsHost = getParameterByName('host');
    logsPort = getParameterByName('port');
    logsAdminPort = getParameterByName('adminPort');
    if ( logsHost && logsAdminPort ) {
        logsInit();
    }
});
