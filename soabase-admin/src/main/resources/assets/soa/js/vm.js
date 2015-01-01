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
var vmGcChart = null;
var vmGcData = {};

var VM_MAX_GC_POINTS = 50;

function vmIsGc(s) {
    return s.search("jvm\.gc\..*\.count") >= 0;
}

function vmUpdateGcChart(gauges) {
    var localC3Data = [];

    for ( var i in gauges ) {
        if ( vmIsGc(i) ) {
            var name = i.substring('jvm.gc.'.length);
            name = name.substring(0, name.length - '.count'.length);
            var thisValue = gauges[i].value;

            var tab = vmGcData[name];
            if ( !tab ) {
                tab = [];
                for ( var j = 0; j < VM_MAX_GC_POINTS; ++j ) {
                    tab.push(thisValue);
                }
                vmGcData[name] = tab;
            }

            tab.push(thisValue);
            if ( tab.length > VM_MAX_GC_POINTS ) {
                tab.shift();
            }

            var c3Data = [name];
            for ( var k = 1; k < tab.length; ++k ) {
                c3Data.push(tab[k] - tab[k - 1]);
            }
            localC3Data.push(c3Data);
        }
    }

    vmGcChart.load({
        columns: localC3Data
    });
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

        vmUpdateGcChart(data.gauges);
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

function vmInit() {
    $('#vm-host').text(vmHost + ':' + vmPort);
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

    vmGcChart = c3.generate({
        bindto: '#vm-gc-chart',
        data: {
            columns: []
        },
        axis: {
            y: {
                show: true,
                label: {
                    text: '# of GCs',
                    position: 'outer-middle'
                }
            },
            x: {
                show: false
            }
        },
        transition: {
            duration: 250
        }
    });

    if ( vmHost && vmPort ) {
        vmInit();
    }
});