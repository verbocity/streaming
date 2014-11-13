function createCharts(jQuery) {
    Highcharts.setOptions({
        global: {
            useUTC: false
        }
    })

    if (!!window.EventSource) {
        var srcLine = new EventSource('http://localhost:8888/api/stream/line');
        var srcFraud = new EventSource('http://localhost:8888/api/stream/fraud');
        createLineChart(srcLine);
        createLineChart2(srcLine);
        createFraudText(srcFraud);
    } else {
      alert('no event source');
    }
}

function zeros(n) {
    // generate an array of random data
    var data = [],
        time = (new Date()).getTime(),
        i;

    for (i = -n; i <= 0; i += 1) {
        data.push({
            x: time + i * 1000,
            y: 0
        });
    }
    return data;
}

function createLineChart(source) {
    var data = []

    $('#container1').highcharts({
        chart: {
            type: 'line',
            animation: Highcharts.svg,
            marginRight: 10,
            events: {
                load: function() {
                    source.addEventListener('message', function(e) {
                    var parsed = $.parseJSON(e.data)

                    //console.log(parsed.shell)

                    var c = $('#container1').highcharts();
                    var x = (new Date()).getTime()
                    c.series[0].addPoint([x, parseInt(parsed.esso) ], true, true);
                    c.series[1].addPoint([x, parseInt(parsed.shell) ], true, true);
                    c.series[2].addPoint([x, parseInt(parsed.total) ], true, true);
                    c.series[3].addPoint([x, parseInt(parsed.bp) ], true, true);

                    }, true)
                }
            }
        },
        series: [{
            name: 'Esso',
            data: (zeros(30))
        }, {
            name: 'Shell',
            data: (zeros(30))
        },
        {
            name: 'Total',
            data: (zeros(30))
         },
         {
            name: 'Bp',
            data: (zeros(30))
         }],
        title: {
            text: 'Shell vs Esso vs Total vs Bp'
        },
        xAxis: {
            type: 'datetime',
            tickPixelInterval: 150
        },
        yAxis: {
            title: {
                text: 'Value'
            },
            plotLines: [{
                value: 0,
                width: 1,
                color: '#808080'
            }]
        },
        tooltip: {
            formatter: function () {
                return '<b>' + this.series.name + '</b><br/>' +
                    Highcharts.dateFormat('%Y-%m-%d %H:%M:%S', this.x) + '<br/>' +
                    Highcharts.numberFormat(this.y, 2);
            }
        },
        legend: {
            enabled: false
        },
        exporting: {
            enabled: false
        }
    });
}

function createLineChart2(source) {
    var data = []

    $('#container2').highcharts({
        chart: {
            type: 'line',
            animation: Highcharts.svg,
            marginRight: 10,
            events: {
                load: function() {
                    source.addEventListener('message', function(e) {
                    var parsed = $.parseJSON(e.data)

                    //console.log(parsed.shell)

                    var c = $('#container2').highcharts();
                    var x = (new Date()).getTime()
                    c.series[0].addPoint([x, parseInt(parsed.ns) ], true, true);
                    }, true)
                }
            }
        },
        series: [{
            name: 'Ns',
            data: (zeros(30))
        }],
        title: {
            text: 'Ns'
        },
        xAxis: {
            type: 'datetime',
            tickPixelInterval: 150
        },
        yAxis: {
            title: {
                text: 'Value'
            },
            plotLines: [{
                value: 0,
                width: 1,
                color: '#808080'
            }]
        },
        tooltip: {
            formatter: function () {
                return '<b>' + this.series.name + '</b><br/>' +
                    Highcharts.dateFormat('%Y-%m-%d %H:%M:%S', this.x) + '<br/>' +
                    Highcharts.numberFormat(this.y, 2);
            }
        },
        legend: {
            enabled: false
        },
        exporting: {
            enabled: false
        }
    });
}

function createFraudText(source) {
    source.addEventListener('message', function(e) {
    var parsed = $.parseJSON(e.data)

    //console.log(parsed.shell)
    var date = new Date(parsed.datetime*1000);
    var hours = date.getHours();
    // minutes part from the timestamp
    var minutes = "0" + date.getMinutes();
    // seconds part from the timestamp
    var seconds = "0" + date.getSeconds();


    // will display time in 10:30:23 format
    var formattedTime = hours + ':' + minutes.substr(minutes.length-2) + ':' + seconds.substr(seconds.length-2);

    $('#fraud1 table').prepend( '<tr><td>' + parsed.country + '</td><td>' + parsed.city + '</td><td> ' + parsed.amount + '</td><td> '+ parsed.description + '</td></tr>')
    }, true)
}