var SOA_SERVICE_ID_PREFIX = 'soa-service-';

function soaToName(instance) {
    return instance.host + ':' + instance.port
}

function soaHandleForceButton(serviceName, localInstance) {
    var template = soaGetTemplate('soa-force-dialog-content');
    bootbox.dialog({
        'message': template,
        'title': 'Force status of "' + serviceName + '" instance: ' + soaToName(localInstance),
        'onEscape': function () {
            bootbox.hideAll();
        },
        'buttons': {
            'cancel': {
                label: "Cancel",
                className: "btn-default"
            },
            'ok': {
                label: "Submit",
                className: "btn-primary",
                callback: function () {
                    var forceValue = $('.soa-force-radios').filter(':checked').val();
                    soaForceDialogSubmit(serviceName, localInstance.id, forceValue);
                }
            }
        }
    });
}

function soaForceDialogSubmit(serviceName, instanceId, forceValue) {
    soaShowInfiniteProgressBar();
    $.ajax({
        type: "PUT",
        url: '/soa/discovery/force/' + serviceName + '/' + instanceId,
        contentType: "application/json",
        data: JSON.stringify(forceValue),
        success: function() {
            soaHideInfiniteProgressBar();
            soaUpdateInstances();
        },
        error: function(jqXHR, textStatus, errorThrown) {
            soaHideInfiniteProgressBar();
            bootbox.alert('Operation failed: ' + errorThrown);
        }
    });
}

function soaShowLogWindow(localInstance, filesData) {
    if ( filesData.length == 0 ) {
        bootbox.alert('No log files found.');
        return;
    }

    var logFiles = "";
    for ( var i in filesData ) {
        logFiles = logFiles + '<option>' + filesData[i].name + '</option>';
    }

    var localTemplate = soaGetTemplate('soa-service-logs', {
        '$FILES$': logFiles
    });
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
        var stoplightGreen = soaGetTemplate('soa-stoplight-set-green');
        var stoplightRed = soaGetTemplate('soa-stoplight-set-red');
        var healthy = soaGetTemplate('soa-service-healthy');
        var unHealthy = soaGetTemplate('soa-service-unhealthy');
        var forced = soaGetTemplate('soa-service-forced');

        function trim(s) {
            var max = 20;
            if ( s.length >= max ) {
                return s.substring(0, max / 2) + '&hellip;' + s.substring(s.length - (max / 2));
            }
            return s;
        }

        var id = SOA_SERVICE_ID_PREFIX + serviceName;
        var divExists = $('#' + id).length > 0;
        if ( data.length > 0 ) {
            if ( !divExists ) {
                var div = document.createElement('div');
                $(div).attr('id', id).appendTo('#soa-services');
            }

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
                var details = (instance.healthyState === 'HEALTHY') ? healthy : unHealthy;
                if ( instance.forcedState != 'CLEARED' ) {
                    details = details + forced.replace('$VALUE$', instance.forcedState.toLowerCase());
                }
                var thisInstance = soaGetTemplate('soa-service-instance-template', {
                    '$STOPLIGHT$': stopLight,
                    '$INSTANCE_DATA$': trim(soaToName(instance)),
                    '$INSTANCE_DETAILS$': details,
                    '$ID$': instance.id
                });
                instances = instances + thisInstance;
            }

            var content = soaGetTemplate('soa-service-template', {
                '$SERVICE_NAME$': serviceName,
                '$SERVICE_QTY$': data.length,
                '$INSTANCES$': instances
            });
            $('#' + id).html(content);
            $('#soa-service-body-toggle-' + serviceName).click(function(){
                $(this).toggleClass('glyphicon-expand glyphicon-collapse-down');
            });

            function setHandlers(instance) {
                $('#soa-force-button-' + instance.id).click(function(){
                    soaHandleForceButton(serviceName, instance);
                });
                $('#soa-logs-button-' + instance.id).click(function(){
                    soaHandleLogButton(serviceName, instance);
                });
            }

            for ( var j in data ) {
                setHandlers(data[j]);
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
    var ourTab = $('#soa-tab-');
    var ourInterval;
    ourTab.bind('soa-show', function(){
        soaUpdateInstances();
        ourInterval = setInterval(soaUpdateInstances, 5000);
    });
    ourTab.bind('soa-hide', function(){
        if ( ourInterval ) {
            clearInterval(ourInterval);
            ourInterval = null;
        }
    });
});