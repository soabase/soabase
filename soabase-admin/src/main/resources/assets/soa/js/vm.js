var VM_DEFAULT_MAX_METRIC_POINTS = 50;
var VM_METRICS_PER_ROW = 3;

var vmMaxMetricPoints = VM_DEFAULT_MAX_METRIC_POINTS;
var vmHost = null;
var vmPort = null;
var vmAdminPort = null;
var vmServiceName = null;
var vmRate = 1000;

function vmMakePrefixSuffixPredicate(prefix, suffix) {
    var minLength = prefix.length + suffix.length;
    return function(obj, name) {
        if ( name.length > minLength ) {
            if ( name.substring(0, prefix.length) === prefix ) {
                if ( name.substring(name.length - suffix.length, name.length) === suffix ) {
                    return {
                        label: name.substring(prefix.length, name.length - suffix.length),
                        value: obj.value
                    };
                }
            }
        }
        return null;
    }
}

function vmMetricList(obj, stack, resultTab, pred) {
    for (var property in obj) {
        if (obj.hasOwnProperty(property)) {
            var val = pred(obj[property], stack + property);
            if ( val ) {
                resultTab.push(val);
            }
            if (typeof obj[property] == "object") {
                vmMetricList(obj[property], stack + property + '.', resultTab, pred);
            }
        }
    }
}

function vmUpdate1Metric(metric, data) {
    var c3Data = [];

    function applyToMetric(label, value) {
        var tab = metric.data[label];
        if ( !tab ) {
            tab = [];
            metric.data[label] = tab;
        }

        if ( tab.length != vmMaxMetricPoints ) {
            tab = [];
            for ( var p = 0; p < vmMaxMetricPoints; ++p ) {
                tab.push(value);
            }
            metric.data[label] = tab;
        }

        tab.push(value);
        if ( tab.length > vmMaxMetricPoints ) {
            tab.shift();
        }

        return tab;
    }

    data.metricList = function(pred) {
        var resultTab = [];
        vmMetricList(data, '', resultTab, pred);
        return resultTab;
    };
    for ( var m in metric.metrics ) {
        var spec = metric.metrics[m];
        var valueTab = null;
        try {
            var thisValue = eval('data.' + spec.path);
            if ( Array.isArray(thisValue) ) {
                valueTab = thisValue;
            } else {
                valueTab = [
                    {
                        label: spec.label,
                        value: thisValue
                    }
                ];
            }
        }
        catch ( e ) {
            valueTab = null;
        }
        if ( (valueTab != undefined) && (valueTab != null) ) {
            for ( var v in valueTab ) {
                var tab = applyToMetric(valueTab[v].label, valueTab[v].value);
                var thisC3Data = [valueTab[v].label];
                for ( var i = 0; i < tab.length; ++i ) {
                    switch ( metric.type ) {
                        case 'DELTA':
                        {
                            if ( i > 0 ) {
                                thisC3Data.push(tab[i] - tab[i - 1]);
                            }
                            break;
                        }

                        case 'PERCENT':
                        {
                            thisC3Data.push(Math.round(100 * tab[i]));
                            break;
                        }

                        default:
                        {
                            thisC3Data.push(tab[i]);
                            break;
                        }
                    }
                }
                c3Data.push(thisC3Data);
            }
        }
    }

    metric.chart.load({
        columns: c3Data
    });
}

function vmUpdateMetrics(data) {
    for ( var i in vmMetrics ) {
        vmUpdate1Metric(vmMetrics[i], data);
    }
}

function vmUpdate() {
    $.getJSON('http://' + vmHost + ':' + vmAdminPort + '/metrics', function (data) {
        var memMax = data.gauges['jvm.memory.heap.max'].value;
        var memUsed = data.gauges['jvm.memory.heap.used'].value;
        var memUsedPercent = Math.max(Math.round(data.gauges['jvm.memory.heap.usage'].value * 100), 1);
        var worker = memUsedPercent;

        var greenPercent = Math.min(worker, 50);
        worker = Math.max(worker - greenPercent, 0);

        var yellowPercent = Math.min(worker, 25);
        worker = Math.max(worker - yellowPercent, 0);

        var redPercent = Math.min(worker, 25);

        $('#vm-progress-green').width(greenPercent + '%');
        $('#vm-progress-yellow').width(yellowPercent + '%');
        $('#vm-progress-red').width(redPercent + '%');
        $('#vm-progress-memory').text(memUsedPercent + '%' + ' - ' + memUsed.toLocaleString() + ' of ' + memMax.toLocaleString());

        vmUpdateMetrics(data);
        $('#vm-last-updated').text('Last updated ' + (new Date()).toLocaleString());
    });
    vmResetTimeout();
}

function vmUpdateMaxMetricPoints() {
    $('#vm-window-size-buttons :button').each(function () {
        if ( parseInt(this.value) === vmMaxMetricPoints ) {
            $(this).addClass('active');
        }
        else {
            $(this).removeClass('active');
        }
    });
    vmResetTimeout();
}

function vmResetTimeout() {
    if ( vmRate ) {
        setTimeout(vmUpdate, vmRate);
    }
}

function vmUpdateInterval() {
    $('#vm-poll-buttons :button').each(function () {
        if ( parseInt(this.value) === vmRate ) {
            $(this).addClass('active');
        }
        else {
            $(this).removeClass('active');
        }
    });
    vmResetTimeout();
}

function vmGetCollapseFunction(metric) {
    return function(){
        $('#vm-metric-collapse-button-' + metric.id).toggleClass('glyphicon-collapse-down glyphicon-expand');
        $('#vm-metric-collapse-' + metric.id).collapse('toggle');
    };
}

function vmBuildMetrics() {
    var currentRow = null;
    var metricCountInRow = VM_METRICS_PER_ROW;
    for ( var i in vmMetrics ) {
        var metric = vmMetrics[i];
        var template = soaGetTemplate('vm-metric-template', {
            '$ID$': metric.id
        });

        if ( metricCountInRow >= VM_METRICS_PER_ROW ) {
            metricCountInRow = 0;
            currentRow = document.createElement('div');
            $(currentRow).addClass('row');
            $(currentRow).appendTo('#vm-metrics-rows');
        }

        ++metricCountInRow;
        var oldHtml = $(currentRow).html();
        $(currentRow).html(oldHtml + '\n' + template);
        $(currentRow).appendTo('#vm-metrics-rows');
        $('#vm-metric-name-' + metric.id).text(metric.name);

        metric.data = [];
    }

    for ( i in vmMetrics ) {
        metric = vmMetrics[i];
        var axisYSpec = {};
        axisYSpec.show = true;
        axisYSpec.label = {
            text: metric.label,
            position: 'outer-middle'
        };
        if ( metric.type === 'PERCENT' ) {
            axisYSpec.min = 0;
            axisYSpec.max = 100;
            axisYSpec.padding = {
                top: 0,
                bottom: 0
            };
        }

        metric.chart = c3.generate({
            bindto: '#vm-metric-' + metric.id,
            data: {
                columns: []
            },
            axis: {
                y: axisYSpec,
                x: {
                    show: false
                }
            },
            transition: {
                duration: 0
            },
            size: {
                height: 200
            }
        });

        $('#vm-metric-collapse-button-' + metric.id).click(vmGetCollapseFunction(metric));
    }
}

function vmRemoveUntargetedMetrics() {
    var newMetrics = [];
    for ( i in vmMetrics ) {
        var metric = vmMetrics[i];
        if ( (metric.targetedServiceName == null) || (metric.targetedServiceName == vmServiceName) ) {
            newMetrics.push(metric);
        }
    }
    vmMetrics = newMetrics;
}

function vmInit() {
    $('#vm-host').text(vmHost + ':' + vmPort);
    vmRemoveUntargetedMetrics();
    vmBuildMetrics();
    vmUpdate();
    vmUpdateInterval();

    $('#vm-poll-buttons :button').each(function () {
        $(this).click(function () {
            vmRate = parseInt(this.value);
            vmUpdateInterval();
        });
    });
    $('#vm-window-size-buttons :button').each(function () {
        $(this).click(function () {
            vmMaxMetricPoints = parseInt(this.value);
            vmUpdateMaxMetricPoints();
        });
    });
}

$(function () {
    vmHost = getParameterByName('host');
    vmPort = getParameterByName('port');
    vmAdminPort = getParameterByName('adminPort');
    vmServiceName = getParameterByName('name');
    soaAutoLoadTemplates();

    if ( vmHost && vmAdminPort ) {
        vmInit();
    }
});