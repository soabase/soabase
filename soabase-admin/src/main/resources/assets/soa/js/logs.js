var logsHost = null;
var logsPort = null;
var logsAdminPort = null;

function logsDisplay(data) {
    var logFiles = "";
    if ( data.length == 0 ) {
        logFiles = '<span class="list-group-item">No log files found.</span>'
    } else {
        for ( var i in data ) {
            var thisData = data[i];
            var url = 'http://' + logsHost + ':' + logsAdminPort + '/api/soa/logging/file/raw/' + thisData.key;
            url = url + "?name=" + encodeURIComponent(thisData.name);
            logFiles = logFiles + '<a href="' + url + '" target="_blank" class="list-group-item">' + thisData.name + '</a>\n';
        }
    }

    $('#logs-list').html(logFiles);
}

function logsInit() {
    $('#logs-host').text(logsHost + ':' + logsPort);

    var url = 'http://' + logsHost + ':' + logsAdminPort + '/api/soa/logging/files';
    $.getJSON(url, function(data){
        logsDisplay(data);
    });
}

$(function(){
    logsHost = getParameterByName('host');
    logsPort = getParameterByName('port');
    logsAdminPort = getParameterByName('adminPort');
    if ( logsHost && logsAdminPort ) {
        logsInit();
    }
});
