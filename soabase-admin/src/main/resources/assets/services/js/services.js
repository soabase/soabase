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
function soaHandleLogButton(localInstance) {
    var url = '/logs?host=' + localInstance.host + '&port=' + localInstance.port + '&adminPort=' + localInstance.adminPort;
    window.open(url, '_blank');
}

function soaHandleTraceButton(localInstance) {
    var url = 'http://' + localInstance.host + ':' + localInstance.adminPort + '/threads';
    window.open(url, '_blank');
}

function soaHandleDetailsButton(localInstance) {
    var url = '/vm?host=' + localInstance.host + '&port=' + localInstance.port + '&adminPort=' + localInstance.adminPort;
    window.open(url, '_blank');
}

function soaServicesBuildContainer(serviceName) {
    var id = SOA_SERVICE_ID_PREFIX + serviceName;

    var div = document.createElement('div');
    $(div).attr('id', id).appendTo('#soa-services');

    var content = soaGetTemplate('soa-service-template', {
        '$SERVICE_NAME$': serviceName
    });
    $('#' + id).html(content);

    $('#soa-service-body-toggle-' + serviceName).click(function(){
        $('#soa-service-body-toggle-' + serviceName).toggleClass('glyphicon-expand glyphicon-collapse-down');
        $('#soa-service-body-collapse-' + serviceName).collapse('toggle');
        return true;
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
        if ( data.length > 0 ) {
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

            $('#soa-service-instance-qty-' + serviceName).text(data.length);
            $('#soa-service-instances-' + serviceName).html(instances);

            function setHandlers(instance) {
                $('#soa-force-button-' + instance.id).click(function(){
                    soaHandleForceButton(serviceName, instance);
                });
                $('#soa-logs-button-' + instance.id).click(function(){
                    soaHandleLogButton(instance);
                });
                $('#soa-trace-button-' + instance.id).click(function(){
                    soaHandleTraceButton(instance);
                });
                $('#soa-details-button-' + instance.id).click(function(){
                    soaHandleDetailsButton(instance);
                });
            }
            for ( var j in data ) {
                setHandlers(data[j]);
            }

            $('#soa-services-last-updated').text('Last updated ' + (new Date()).toLocaleString());
        } else {
            $('#' + id).remove();
        }
    });
}

var soaServices = [];

function soaServiceDivExists(serviceName) {
    var id = SOA_SERVICE_ID_PREFIX + serviceName;
    return $('#' + id).length > 0;
}

function soaUpdateInstances() {
    $.getJSON('/soa/discovery/services', function(data){
        var id = SOA_SERVICE_ID_PREFIX + serviceName;
        var serviceName;
        var i;

        // remove services that don't exist anymore
        for ( i in soaServices ) {
            serviceName = soaServices[i];
            if ( !data[serviceName] && soaServiceDivExists(serviceName) ) {
                $('#' + id).remove();
            }
        }

        for (i in data) {
            serviceName = data[i];
            if ( !soaServiceDivExists(serviceName) ) {
                soaServicesBuildContainer(serviceName);
            }
            soaUpdateInstancesForService(serviceName);
        }

        soaServices = data;
    });
}

$(function() {
    var ourTab = $('#soa-tab-');
    soaUpdateInstances();
    setInterval(soaUpdateInstances, 12345);
});