var SOA_SERVICE_ID_PREFIX = 'soa-service-';

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
                thisInstance = thisInstance.replace('$INSTANCE_DATA$', instance.host + ':' + instance.port);

                var details = instance.healthyState;
                if ( instance.forcedState != 'CLEARED' ) {
                    details = details + " - " + instance.forcedState;
                }
                thisInstance = thisInstance.replace('$INSTANCE_DETAILS$', details);
                thisInstance = thisInstance.replace('$ID$', instance.id);

                instances = instances + thisInstance;
            }

            template = $('#soa-service-template').html();
            var content = template.replace('$SERVICE_NAME$', serviceName);
            content = content.replace('$SERVICE_QTY$', data.length);
            content = content.replace('$INSTANCES$', instances);
            $('#' + id).html(content);

            for ( i in data ) {
                instance = data[i];
                $('#soa-force-button-' + instance.id).click(function(){
                    window.alert('hey');
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