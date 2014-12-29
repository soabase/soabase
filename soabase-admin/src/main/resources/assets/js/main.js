var SOA_SERVICE_ID_PREFIX = 'soa-service-';

function soaToName(instance) {
    return instance.host + ':' + instance.port
}

function soaForceInstance(serviceName, instanceId, forceValue) {
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
                    var buttonTemplate = $('#soa-force-buttons').html();
                    buttonTemplate = buttonTemplate.replace(/\$ID\$/g, localInstance.id);
                    bootbox.dialog({
                        'message': buttonTemplate,
                        'title': 'Force status of ' + soaToName(localInstance),
                        'onEscape': function(){
                            $('#soa-force-button-' + localInstance.id).modal('hide');
                        },
                        'buttons': {
                            'cancel': {
                                label: "Cancel",
                                className: "btn-default"
                            },
                            'ok': {
                                label: "OK",
                                className: "btn-primary",
                                callback: function () {
                                    var value = $('input:radio[name=' + ('soa-force-radios-' + localInstance.id) + ']:checked').val();
                                    soaForceInstance(serviceName, localInstance.id, value);
                                }
                            }
                        }
                    });
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
    $('#soa-tab-').bind('soa-show', function(){
        soaUpdateInstances();
    });
});