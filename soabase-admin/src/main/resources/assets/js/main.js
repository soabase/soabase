var SOA_SERVICE_ID_PREFIX = 'soa-service-';

function soaUpdateInstancesForService(serviceName) {
    $.getJSON('/soa/discovery/all/' + serviceName, function(data){
        var id = SOA_SERVICE_ID_PREFIX + serviceName;
        var divExists = $('#' + id).length > 0;
        if ( data.length > 0 ) {
            if ( !divExists ) {
                var content = $('#soa-services').html();
                var template = $('#soa-service-template').html();
                content = content + template.replace('$SERVICE_NAME$', serviceName);
                $('#soa-services').html(content);
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