function getParameterByName(name) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
        results = regex.exec(location.search);
    return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}

var vmHost = null;
var vmPort = null;
var vmRate = 1000;
var vmInterval = null;

var VM_MAX_METRIC_POINTS = 50;
var VM_METRICS_PER_ROW = 3;

function vmUpdate1Metric(metric, gauges) {
    var c3Data = [];

    for ( var gaugeName in gauges ) {
        var prefixMatch = gaugeName.match(metric.prefix);
        var suffixMatch = gaugeName.match(metric.suffix);
        if ( prefixMatch && suffixMatch ) {
            prefixMatch = prefixMatch.toString();
            suffixMatch = suffixMatch.toString();
            var name = gaugeName.substring(prefixMatch.length, gaugeName.length - suffixMatch.length);
            if ( !name || (name.length == 0) ) {
                name = prefixMatch;
            }
            var thisValue = gauges[gaugeName].value;

            var i;
            var tab = metric.data[name];
            if ( !tab ) {
                tab = [];
                metric.data[name] = tab;
                for ( i = 0; i < VM_MAX_METRIC_POINTS; ++i ) {
                    tab.push(thisValue);
                }
            }
            tab.push(thisValue);
            if ( tab.length > VM_MAX_METRIC_POINTS ) {
                tab.shift();
            }

            var thisC3Data = [name];
            for ( i = 0; i < tab.length; ++i ) {
                switch ( metric.type ) {
                    case 'DELTA': {
                        if ( i > 0 ) {
                            thisC3Data.push(tab[i] - tab[i - 1]);
                        }
                        break;
                    }

                    case 'PERCENT': {
                        thisC3Data.push(Math.round(100 * tab[i]));
                        break;
                    }

                    default: {
                        thisC3Data.push(tab[i]);
                        break;
                    }
                }
            }
            c3Data.push(thisC3Data);
        }
    }

    metric.chart.load({
        columns: c3Data
    });
}

function vmUpdateMetrics(gauges) {
    for ( var i in vmMetrics ) {
        vmUpdate1Metric(vmMetrics[i], gauges);
    }
}

function vmUpdate() {
    $.getJSON('http://' + vmHost + ':' + vmPort + '/metrics', function (data) {
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

        vmUpdateMetrics(data.gauges);
    });
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
    if ( vmInterval ) {
        clearInterval(vmInterval);
        vmInterval = null;
    }
    if ( vmRate ) {
        vmInterval = setInterval(vmUpdate, vmRate);
    }
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
                duration: 250
            },
            size: {
                height: 200
            }
        });
    }
}

function vmInit() {
    $('#vm-host').text(vmHost + ':' + vmPort);
    vmBuildMetrics();
    vmUpdate();
    vmUpdateInterval();

    $('#vm-poll-buttons :button').each(function () {
        $(this).click(function () {
            vmRate = parseInt(this.value);
            vmUpdateInterval();
        });
    });
}

$(function () {
    vmHost = getParameterByName('host');
    vmPort = getParameterByName('port');
    soaAutoLoadTemplates();

    if ( vmHost && vmPort ) {
        vmInit();
    }
});