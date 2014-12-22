var SOA_SERVICE_ID_PREFIX = 'soa-service-';

function soaUpdateInstancesForService(serviceName) {
    $.getJSON('/soa/discovery/all/' + serviceName, function(data){
        var stoplightGreen = $('#soa-stoplight-set-green').html();
        var stoplightYellow = $('#soa-stoplight-set-yellow').html();
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
                instances = instances + template.replace('$CONTENT$', stoplightGreen + data[i].host + ':' + data[i].port);
            }

            template = $('#soa-service-template').html();
            var content = template.replace('$SERVICE_NAME$', serviceName);
            content = content.replace('$SERVICE_QTY$', data.length);
            content = content.replace('$INSTANCES$', instances);
            $('#' + id).html(content);
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