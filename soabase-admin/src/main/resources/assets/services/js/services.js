var soaActiveServiceName = null;
var soaActiveDeploymentGroups = [];

function soaActivateDialogSubmit(serviceName, groupName, ableValue) {
    soaShowInfiniteProgressBar();
    $.ajax({
        type: 'PUT',
        url: '/soa/discovery/deploymentGroup/' + serviceName + '/' + groupName,
        contentType: "application/json",
        data: JSON.stringify(ableValue),
        success: function() {
            soaHideInfiniteProgressBar();
            soaServicesUpdateDetailsAndGroups(serviceName);
        },
        error: function(jqXHR, textStatus, errorThrown) {
            soaHideInfiniteProgressBar();
            bootbox.alert('Operation failed: ' + errorThrown);
        }
    });
}

function soaGroupIsActive(groupName) {
    if ( soaActiveDeploymentGroups[groupName] === undefined ) {
        return true;
    }
    return soaActiveDeploymentGroups[groupName];
}

function soaSortAndOrganizeInstances(data) {
    var instances = [];

    function pushTo(name, instance) {
        if ( !instances[name] ) {
            instances[name] = [];
        }
        instances[name].push(instance);
    }

    for ( var i in data ) {
        var instance = data[i];
        var deploymentGroup = (instance.metaData && instance.metaData['soabase-deployment-group']) ? instance.metaData['soabase-deployment-group'] : '';
        var metaDataTab = deploymentGroup.length ? deploymentGroup.split(',') : [''];
        for ( var j in metaDataTab ) {
            var group = metaDataTab[j];
            pushTo(group, instance);
        }
    }
    return instances;
}

var soaTempActive = [];
var soaTempInactive = [];

function soaActivationsUpdate() {
    function buildButtonList(tab, fromTabName, toTabName, buttonType, icon) {
        var list = '';
        for ( var i in tab ) {
            var name = tab[i];
            list = list + soaGetTemplate('soa-service-activation-item-button', {
                '$NAME$': name,
                '$DISPLAY_NAME$': soaDisplayGroupName(name),
                '$FROM_TAB$': fromTabName,
                '$TO_TAB$': toTabName,
                '$BUTTON_TYPE$': buttonType,
                '$ICON$': icon
            });
        }
        return list;
    }

    $('#soa-manage-activations-dialog-active').html(buildButtonList(soaTempActive, 'soaTempActive', 'soaTempInactive', 'btn-success', 'glyphicon-chevron-down'));
    $('#soa-manage-activations-dialog-inactive').html(buildButtonList(soaTempInactive, 'soaTempInactive', 'soaTempActive', 'btn-danger', 'glyphicon-chevron-up'));
}

function soaActivationsitemClick(fromTab, toTab, name) {
    var index = fromTab.indexOf(name);
    if ( index >= 0 ) {
        fromTab.splice(index, 1);
    }
    if ( toTab.indexOf(name) < 0 ) {
        toTab.push(name);
    }
    soaActivationsUpdate();
}

function soaActivationsSubmit() {
    var data = [];
    for ( var i in soaTempActive ) {
        data.push({
            name: soaTempActive[i],
            active: true
        });
    }
    for ( var i in soaTempInactive ) {
        data.push({
            name: soaTempInactive[i],
            active: false
        });
    }

    soaShowInfiniteProgressBar();
    $.ajax({
        type: 'POST',
        url: '/soa/discovery/deploymentGroup/' + soaActiveServiceName,
        contentType: "application/json",
        data: JSON.stringify(data),
        success: function() {
            soaHideInfiniteProgressBar();
            soaServicesUpdateDetailsAndGroups(soaActiveServiceName);
        },
        error: function(jqXHR, textStatus, errorThrown) {
            soaHideInfiniteProgressBar();
            bootbox.alert('Operation failed: ' + errorThrown);
        }
    });
}

function soaHandleManageActivationsButton() {
    soaTempActive = [];
    soaTempInactive = [];
    for ( var name in soaActiveDeploymentGroups ) {
        if ( soaActiveDeploymentGroups[name] ) {
            soaTempActive.push(name);
        } else {
            soaTempInactive.push(name);
        }
    }

    var template = soaGetTemplate('soa-manage-activations-dialog-content');
    bootbox.dialog({
        'message': template,
        'title': 'Manage activations for service: ' + soaActiveServiceName,
        'onEscape': function () {
            bootbox.hideAll();
        },
        'buttons': {
            'cancel': {
                label: "Cancel",
                className: "btn-default"
            },
            'ok': {
                label: "Change Immediately",
                className: "btn-danger",
                callback: function () {
                    soaActivationsSubmit();
                }
            }
        }
    });

    soaActivationsUpdate();

    function localFixName(name) {
        if ( name === '*' ) {
            name = '';
        }
        return name;
    }

    $('#soa-manage-activations-add-button').click(function(){
        var list = $('#soa-activation-group-names').val().split(' ');
        for ( var i in list ) {
            var name = localFixName(list[i]);
            soaActivationsitemClick(soaTempInactive, soaTempActive, name);
            $('#soa-activation-group-names').val('');
        }
        return true;
    });

    function removeItem(tab, name) {
        var index = tab.indexOf(name);
        if ( index >= 0 ) {
            tab.splice(index, 1);
        }
    }

    $('#soa-manage-activations-remove-button').click(function(){
        var list = $('#soa-activation-group-names').val().split(' ');
        for ( var i in list ) {
            var name = localFixName(list[i]);
            removeItem(soaTempActive, name);
            removeItem(soaTempInactive, name);
            $('#soa-activation-group-names').val('');
            soaActivationsUpdate();
        }
        return true;
    });
}

function soaHandleActivationButton(serviceName, groupName) {
    var displayGroupName = soaDisplayGroupName(groupName);
    var template = soaGetTemplate('soa-activate-dialog-content', {
        '$GROUP$': displayGroupName,
        '$SERVICE$': serviceName
    });
    bootbox.dialog({
        'message': template,
        'title': 'Change group "' + displayGroupName + '" for service: ' + serviceName,
        'onEscape': function () {
            bootbox.hideAll();
        },
        'buttons': {
            'cancel': {
                label: "Cancel",
                className: "btn-default"
            },
            'ok': {
                label: "Change Immediately",
                className: "btn-danger",
                callback: function () {
                    var ableValue = $('.soa-activate-radios').filter(':checked').val();
                    soaActivateDialogSubmit(serviceName, groupName, ableValue === 'true');
                }
            }
        }
    });
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
        type: 'PUT',
        url: '/soa/discovery/force/' + serviceName + '/' + instanceId,
        contentType: "application/json",
        data: JSON.stringify(forceValue),
        success: function() {
            soaHideInfiniteProgressBar();
            soaServicesUpdateDetailsAndGroups(serviceName);
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

function soaServicesDetailsDisplayGroups(serviceName, data) {
    $('#soa-services-qty').text(data.length);
    if ( data.length > 0 ) {
        var instances = soaSortAndOrganizeInstances(data);

        var instancesContent = "";
        var index = 0;
        for ( var groupName in instances ) {
            instancesContent = instancesContent + soaServicesGenerateDetails(instances[groupName], groupName, index);
            index = index + 1;
        }
        $('#soa-services-detail-instances').html(instancesContent);

        function setHandlers(instance, groupIndex) {
            var localId = (instance.id + '_' + groupIndex);
            $('#soa-force-button-' + localId).click(function(){
                soaHandleForceButton(soaActiveServiceName, instance);
                return true;
            });
            $('#soa-logs-button-' + localId).click(function(){
                soaHandleLogButton(instance);
                return true;
            });
            $('#soa-trace-button-' + localId).click(function(){
                soaHandleTraceButton(instance);
                return true;
            });
            $('#soa-details-button-' + localId).click(function(){
                soaHandleDetailsButton(soaActiveServiceName, instance);
                return true;
            });
        }

        function setActivationHandler(groupName, groupIndex) {
            $('#soa-activation-button-' + groupIndex).click(function(){
                soaHandleActivationButton(serviceName, groupName);
                return true;
            });
        }

        index = 0;
        for ( groupName in instances ) {
            var instancesTab = instances[groupName];
            for ( var i in instancesTab ) {
                setHandlers(instancesTab[i], index);
            }

            setActivationHandler(groupName, index);

            index = index + 1;
        }
    } else {
        $('#soa-services-detail-instances').html(soaGetTemplate('soa-services-no-instances'));
    }
}

function soaDisplayGroupName(groupName) {
    if ( groupName === '' ) {
        return '<em>Default</em>';
    }
    return groupName;
}

function soaServicesGenerateDetails(data, groupName, groupIndex) {
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
            '$ID$': (instance.id + '_' + groupIndex)
        });
        instances = instances + thisInstance;
    }

    var isActive = soaGroupIsActive(groupName);
    return soaGetTemplate('soa-services-detail-service-container', {
        '$GROUP_NAME$': soaDisplayGroupName(groupName),
        '$INSTANCES$': instances,
        '$ID$': groupIndex,
        '$PANEL_TYPE$': (isActive ? 'panel-primary' : 'panel-danger'),
        '$ACTIVE_COLOR$': (isActive ? 'soa-green' : 'soa-red'),
        '$BUTTON_NAME$':  (isActive ? 'Deactivate...' : 'Activate...')
    });
}

function soaServicesCloseDetails() {
    soaActiveServiceName = null;
    $('#soa-services-qty').text();

    $('#soa-services-brumb-detail').hide();
    $('#soa-services-brumb-main').show();
    $('#soa-services-back-container').hide();

    $('#soa-services-carousel').carousel('prev');
}

function soaServicesDetails(serviceName) {
    soaActiveServiceName = serviceName;

    $('#soa-services-carousel').carousel('next');

    $('#soa-services-detail-instances').html(soaGetTemplate('soa-services-loading-template'));
    $('#soa-services-detail-qty').text('');
    $('#soa-services-back-container').show();

    $('#soa-services-brumb-main').hide();
    $('#soa-services-brumb-detail-service').text(serviceName);
    $('#soa-services-brumb-detail').show();

    soaServicesUpdateDetailsAndGroups(serviceName);
}

function soaServicesUpdateDetailsAndGroups(serviceName) {
    $.ajax({
        type: 'GET',
        url: '/soa/discovery/deploymentGroups/' + serviceName,
        success: function(data){
            soaActiveDeploymentGroups = data;
            soaServicesUpdateDetails(serviceName);
        },
        error: function(jqXHR, textStatus, errorThrown) {
            soaHideInfiniteProgressBar();
            bootbox.alert('Operation failed: ' + errorThrown);
        }
    });
}

function soaServicesUpdateDetails(serviceName) {
    $.ajax({
        type: 'GET',
        url: '/soa/discovery/all/' + serviceName,
        success: function(data){
            soaServicesDetailsDisplayGroups(serviceName, data);
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
            soaServicesUpdateDetailsAndGroups(soaActiveServiceName);
        }

        $('#soa-services-last-updated').text('Last updated ' + (new Date()).toLocaleString());
    });
}

$(function() {
    $('#soa-services-back-button').click(function(){
        soaServicesCloseDetails();
        return true;
    });

    $('#soa-services-manage-activations-button').click(function(){
        soaHandleManageActivationsButton();
        return true;
    });

    soaUpdateServices();
    setInterval(soaUpdateServices, 4987);
});
