var SOA_SERVICE_ID_PREFIX = 'soa-service-';

function soaToName(instance) {
    return instance.host + ':' + instance.port
}

function soaHandleForceButton(serviceName, localInstance) {
    $('#soa-force-dialog-instance-id').val(localInstance.id);
    $('#soa-force-dialog-service-name').val(serviceName);
    $('#soa-force-dialog .modal-title').text('Force status of ' + soaToName(localInstance));
    $('#soa-force-dialog .soa-force-radios').filter('[value="CLEARED"]').prop('checked', true);
    $('#soa-force-dialog').modal('show');
}

function soaForceDialogSubmit() {
    var serviceName = $('#soa-force-dialog-service-name').val();
    var instanceId = $('#soa-force-dialog-instance-id').val();
    var forceValue = $('#soa-force-dialog .soa-force-radios').filter(':checked').val();

    soaShowInfiniteProgressBar();
    $.ajax({
        type: "PUT",
        url: '/soa/discovery/force/' + serviceName + '/' + instanceId,
        contentType: "application/json",
        data: JSON.stringify(forceValue),
        success: function() {
            soaHideInfiniteProgressBar();
        },
        error: function() {
            soaHideInfiniteProgressBar();
            // TODO
        }
    });
}

var soaLogsDialogTemplate = '';
function soaShowLogWindow(localInstance, filesData) {
    if ( filesData.length == 0 ) {
        bootbox.alert('No log files found.');
        return;
    }

    var logFiles = "";
    for ( var i in filesData ) {
        logFiles = logFiles + '<option>' + filesData[i].name + '</option>';
    }

    var localTemplate = soaLogsDialogTemplate.replace('$FILES$', logFiles);
    bootbox.dialog({
        'message': localTemplate,
        'title': 'Logs for ' + soaToName(localInstance),
        'onEscape': function () {
            bootbox.hideAll();
        },
        'buttons': {
            'cancel': {
                label: "Cancel",
                className: "btn-default"
            },
            'ok': {
                label: "Open",
                className: "btn-primary",
                callback: function () {
                    var index = $('#soa-service-logs-file')[0].selectedIndex;
                    var url = 'http://' + localInstance.host + ':' + localInstance.adminPort + '/api/soa/logging/file/raw/' + filesData[index].key;
                    url = url + "?host=" + localInstance.host + "&name=" + encodeURIComponent(filesData[index].name);
                    window.open(url, '_blank');
                }
            }
        }
    });
}

function soaHandleLogButton(serviceName, localInstance) {
    soaShowInfiniteProgressBar();
    var url = 'http://' + localInstance.host + ':' + localInstance.adminPort + '/api/soa/logging/files';
    $.getJSON(url, function(data){
        soaHideInfiniteProgressBar();
        soaShowLogWindow(localInstance, data);
    });
}

function soaUpdateInstancesForService(serviceName) {
    $.getJSON('/soa/discovery/all/' + serviceName, function(data){
        var stoplightGreen = $('#soa-stoplight-set-green').html();
        var stoplightRed = $('#soa-stoplight-set-red').html();

        var id = SOA_SERVICE_ID_PREFIX + serviceName;
        var divExists = $('#' + id).length > 0;
        if ( data.length > 0 ) {
            if ( !divExists ) {
                var div = document.createElement('div');
                $(div).attr('id', id).appendTo('#soa-services');
            }

            var template = $('#soa-service-instance-template').html();
            var instances = "";
            for ( var i in data ) {
                var instance = data[i];
                var isDiscoverable;
                if ( instance.forcedState != 'CLEARED' ) {
                    isDiscoverable = (instance.forcedState === 'REGISTER');
                } else {
                    isDiscoverable = (instance.healthyState === 'HEALTHY');
                }
                var stopLight = isDiscoverable ? stoplightGreen : stoplightRed;
                var thisInstance = template.replace('$STOPLIGHT$', stopLight);
                thisInstance = thisInstance.replace('$INSTANCE_DATA$', soaToName(instance));

                var details = instance.healthyState;
                if ( instance.forcedState != 'CLEARED' ) {
                    details = details + " - " + instance.forcedState;
                }
                thisInstance = thisInstance.replace('$INSTANCE_DETAILS$', details);
                thisInstance = thisInstance.replace(/\$ID\$/g, instance.id);

                instances = instances + thisInstance;
            }

            template = $('#soa-service-template').html();
            var content = template.replace(/\$SERVICE_NAME\$/g, serviceName);
            content = content.replace('$SERVICE_QTY$', data.length);
            content = content.replace('$INSTANCES$', instances);
            $('#' + id).html(content);
            $('#soa-service-body-toggle-' + serviceName).click(function(){
                $(this).toggleClass('glyphicon-expand glyphicon-collapse-down');
            });

            for ( i in data ) {
                var localInstance = data[i];
                $('#soa-force-button-' + localInstance.id).click(function(){
                    soaHandleForceButton(serviceName, localInstance);
                });
                $('#soa-logs-button-' + localInstance.id).click(function(){
                    soaHandleLogButton(serviceName, localInstance);
                });
            }
        } else {
            $('#' + id).remove();
        }
    });
}

function soaUpdateInstances() {
    $.getJSON('/soa/discovery/services', function(data){
        // TODO remove services that don't exist anymore
        for (var i in data) {
            var serviceName = data[i];
            soaUpdateInstancesForService(serviceName);
        }
    });
}

$(function() {
    soaLogsDialogTemplate = $('#soa-service-logs').html();
    $('#soa-service-logs').remove();

    $('#soa-force-dialog-submit').click(function(){
        $('#soa-force-dialog').modal('hide');
        soaForceDialogSubmit();
        return true;
    });

    $('#soa-tab-').bind('soa-show', function(){
        soaUpdateInstances();
    });
});