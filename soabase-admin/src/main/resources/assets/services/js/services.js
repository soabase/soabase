var soaActiveServiceName = null;

function soaHandleActivationButton(groupName) {

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
            soaServicesUpdateDetails(serviceName);
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

function soaHandleDetailsButton(serviceName, localInstance) {
    var url = '/vm?host=' + localInstance.host + '&port=' + localInstance.port + '&adminPort=' + localInstance.adminPort + '&name=' + serviceName;
    window.open(url, '_blank');
}

function soaToName(instance) {
    return instance.host + ':' + instance.port
}

function soaServicesDetailsDisplay(data) {
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
            var thisInstance = soaGetTemplate('soa-services-instance-template', {
                '$STOPLIGHT$': stopLight,
                '$INSTANCE_DATA$': trim(soaToName(instance)),
                '$INSTANCE_DETAILS$': details,
                '$ID$': instance.id
            });
            instances = instances + thisInstance;
        }

        $('#soa-services-qty').text(data.length);
        $('#soa-services-detail-instances').html(instances);

        function setHandlers(instance) {
            $('#soa-force-button-' + instance.id).click(function(){
                soaHandleForceButton(soaActiveServiceName, instance);
            });
            $('#soa-logs-button-' + instance.id).click(function(){
                soaHandleLogButton(instance);
            });
            $('#soa-trace-button-' + instance.id).click(function(){
                soaHandleTraceButton(instance);
            });
            $('#soa-details-button-' + instance.id).click(function(){
                soaHandleDetailsButton(soaActiveServiceName, instance);
            });
        }
        for ( var j in data ) {
            setHandlers(data[j]);
        }
    } else {
        $('#soa-services-detail-instances').html(soaGetTemplate('soa-services-no-instances'));
    }
}

function soaServicesCloseDetails() {
    soaActiveServiceName = null;
    $('#soa-services-qty').text();

    $('#soa-services-brumb-detail').hide();
    $('#soa-services-brumb-main').show();

    $('#soa-services-carousel').carousel('prev');
}

function soaServicesDetails(serviceName) {
    soaActiveServiceName = serviceName;

    $('#soa-services-detail-instances').html(soaGetTemplate('soa-services-loading-template'));
    $('#soa-services-detail-qty').text('');

    $('#soa-services-brumb-main').hide();
    $('#soa-services-brumb-detail-service').text(serviceName);
    $('#soa-services-brumb-detail').show();

    $('#soa-services-carousel').carousel('next');

    soaServicesUpdateDetails(serviceName);
}

 function soaServicesUpdateDetails(serviceName) {
    $.ajax({
        type: 'GET',
        url: '/soa/discovery/all/' + serviceName,
        success: function(data){
            soaServicesDetailsDisplay(data);
        },
        error: function(jqXHR, textStatus, errorThrown) {
            soaHideInfiniteProgressBar();
            bootbox.alert('Operation failed: ' + errorThrown);
        }
    });
}

function soaUpdateServices() {
    $.getJSON('/soa/discovery/services', function(data){
        var content = "";
        for (i in data) {
            var serviceName = data[i];
            var thisRow = soaGetTemplate('soa-services-row-template', {
                '$SERVICE_NAME$': serviceName
            });
            content = content + thisRow;
        }
        $('#soa-services-list').html(content);

        if ( data.length == 0 ) {
            $('#soa-services-list').hide();
            $('#soa-services-list-click-message').hide();
            $('#soa-no-services').show();
        } else {
            $('#soa-services-list').show();
            $('#soa-services-list-click-message').show();
            $('#soa-no-services').hide();
        }

        if ( soaActiveServiceName ) {
            soaServicesUpdateDetails(soaActiveServiceName);
        }

        $('#soa-services-last-updated').text('Last updated ' + (new Date()).toLocaleString());
    });
}

$(function() {
    $('#soa-services-detail-service-close').click(function(){
        soaServicesCloseDetails();
    });

    soaUpdateServices();
    setInterval(soaUpdateServices, 4987);
});
