function createCharts(jQuery) {
    Highcharts.setOptions({
        global: {
            useUTC: false
        }
    })

    if (!!window.EventSource) {
        var srcNL     = new EventSource('http://localhost:8888/api/stream/NL');
        var srcEurope = new EventSource('http://localhost:8888/api/stream/europe');
        var srcWorld  = new EventSource('http://localhost:8888/api/stream/world');
        var srcStats  = new EventSource('http://localhost:8888/api/stream/stats');
        var srcLine   = new EventSource('http://localhost:8888/api/stream/line');

        createNetherlandsMap(srcNL);
        // createEuropeMap(srcEurope);
        createWorldMap(srcWorld);
        updateStatistics(srcStats);

        // var column = createColumnChart(srcLine);
        // $('#container3').highcharts(column);

        // var line = createLineChart(srcLine);
        // $('#container4').highcharts(line);

    } else {
      alert('no event source');
    }
}

function createNetherlandsMap(source) {
    var data = []

    $('#container2').highcharts('Map', {
        chart: {
            animation: false,
            events: {
                load: function() {
                    source.addEventListener('message', function(e) {
                        data = []
                        var parsed = $.parseJSON(e.data)

                        $.each(parsed, function (a, b) {
                            data.push({
                                'hc-key': b["hc-key"],
                                'value':  parseInt(b.value)
                            });
                        });

                        var chart = $('#container2').highcharts();
                        chart.series[0].setData(data);
                    }, false);
                }
            }
        },
        legend: {
            enabled: false
        },
        title: {
            text: 'Transactions in  the Netherlands'
        },
        mapNavigation: {
            enabled: false
        },
        colorAxis: {
            min: 1,
            max: 100,
            type: 'logarithmic',
            minColor: '#ffffff',
            maxColor: '#ff6600'
        },
        series: [{
            data: data,
            color: '#FF6600',
            allAreas: true,
            mapData: Highcharts.maps['countries/nl/nl-all-all'],
            joinBy: 'hc-key',
            name: 'Number of transactions',
            states: {
                hover: {
                    color: '#ff6600'
                }
            },
            tooltip: {
                valueSuffix: '/second'
            }
        }]
    });
}

function createEuropeMap(source) {
    var data = []

    $('#container3').highcharts('Map', {
        chart: {
            animation: false,
            events: {
                load: function() {
                    source.addEventListener('message', function(e) {
                        data = []
                        var parsed = $.parseJSON(e.data)

                        $.each(parsed, function (a, b) {
                            data.push({
                                'hc-key': b["hc-key"].toLowerCase(),
                                'value':  parseInt(b.value)
                            });
                        });

                        var chart = $('#container3').highcharts();
                        chart.series[0].setData(data);
                    }, false);
                }
            }
        },
        title: {
            text: 'Transactions in Europe'
        },
        legend: {
            enabled: false
        },
        mapNavigation: {
            enabled: false
        },
        colorAxis: {
            min: 1,
            max: 1000,
            type: 'logarithmic',
            minColor: '#ffffff',
            maxColor: '#ff6600'
        },
        series: [{
            data: data,
            color: '#FF6600',
            allAreas: true,
            mapData: Highcharts.maps['custom/europe'],
            joinBy: 'hc-key',
            name: 'Number of transactions',
            states: {
                hover: {
                    color: '#ff6600'
                }
            },
            tooltip: {
                valueSuffix: '/second'
            }
        }]
    });
}

function createWorldMap(source) {
    var data = []

    $('#container1').highcharts('Map', {
        chart: {
            animation: false,
            events: {
                load: function() {
                    source.addEventListener('message', function(e) {
                        data = []
                        var parsed = $.parseJSON(e.data)

                        $.each(parsed, function (a, b) {
                            data.push({
                                'hc-key': b["hc-key"],
                                'value':  parseInt(b.value)
                            });
                        });

                        var chart = $('#container1').highcharts();
                        chart.series[0].setData(data);
                    }, false);
                }
            }
        },
        title: {
            text: 'Transactions worldwide'
        },
        legend: {
            enabled: false
        },
        mapNavigation: {
            enabled: false
        },
        colorAxis: {
            min: 1,
            max: 1000,
            type: 'logarithmic',
            minColor: '#ffffff',
            maxColor: '#ff6600'
        },
        series: [{
            data: data,
            color: '#FF6600',
            allAreas: true,
            mapData: Highcharts.maps['custom/world'],
            joinBy: ['iso-a2', 'hc-key'],
            name: 'Number of transactions',
            states: {
                hover: {
                    color: '#ff6600'
                }
            },
            tooltip: {
                valueSuffix: '/second'
            }
        }]
    });
}

function updateStatistics(source) {
    source.addEventListener('message', function(e) {
        parsed = $.parseJSON(e.data)

        var time = parseInt(parsed["currentTime"])
        // multiplied by 1000 so that the argument is in milliseconds, not seconds
        var date = new Date(time * 1000);
        var hours = date.getHours();
        var minutes = "0" + date.getMinutes();
        var seconds = "0" + date.getSeconds();
        var timeText = hours + ':' + minutes.substr(minutes.length - 2) + ':' + seconds.substr(seconds.length - 2);
        $('#currentTime').text(timeText)

        var tranCountThisWindow = parsed["tranCountThisWindow"]
        $('#tranCountThisWindow').text(tranCountThisWindow)

        var tranCountTotal = parsed["tranCountTotal"]
        $('#tranCountTotal').text(tranCountTotal)

        var sumAmountThisWindow = parsed["sumAmountThisWindow"]
        $('#sumAmountThisWindow').text(sumAmountThisWindow)

        var sumAmountTotal = parsed["sumAmountTotal"]
        $('#sumAmountTotal').text(sumAmountTotal)

        var avgAmountThisWindow = parsed["avgAmountThisWindow"]
        $('#avgAmountThisWindow').text(avgAmountThisWindow)

        var avgTransPerSecThisWindow = parsed["avgTransPerSecThisWindow"]
        $('#avgTransPerSecThisWindow').text(avgTransPerSecThisWindow)

        var peakTranCount = parsed["peakTranCount"]
        $('#peakTranCount').text(peakTranCount)

        var mostFrequentVendor = parsed["mostFrequentVendor"]
        $('#mostFrequentVendor').text(mostFrequentVendor)

        var mostFrequentCategory = parsed["mostFrequentCategory"]
        $('#mostFrequentCategory').text(mostFrequentCategory)
    })
}

/*
function createColumnChart(source) {
    var columnchart = {
        chart: {
            type: 'column',
            animation: Highcharts.svg, // don't animate in old IE
            marginRight: 10,
            events: {
                load: function() {
                    source.addEventListener('message', function(e) {
                        var data = JSON.parse(e);
                        var chart = $('#container3').highcharts();
                        chart.series[0].setData(data);
                    }, false);
                }
            }
        },
        plotOptions: {
            series: {
                color: '#FA6600'
            }
        },
        title: {
            text: 'Transaction type'
        },
        xAxis: {
            categories: [1, 2, 3, 4],
            title: {
                text: 'Transaction type'
            },
        },
        yAxis: {
            min: 0,
            max: 10000,
            title: {
                text: 'Number of transactions'
            },
        },
        legend: {
            enabled: false
        },
        exporting: {
            enabled: false
        },
        series: [{
            name: 'Transaction types',
            data: [0, 0, 0, 0]
        }]
    };

    return columnchart
}

function createLineChart(source) {
    var linechart = {
        chart: {
            type: 'spline',
            animation: Highcharts.svg,
            marginRight: 10,
            events: {
                load: function () {
                    // set up the updating of the chart each second
                    var series = this.series[0];
                    setInterval(function () {
                        var x = (new Date()).getTime(), // current time
                            y = Math.random();
                        series.addPoint([x, y], true, true);
                    }, 1000);
                }
            }
        },
        plotOptions: {
            series: {
                color: '#FA6600'
            }
        },
        title: {
            text: 'Live random data'
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
        legend: {
            enabled: false
        },
        exporting: {
            enabled: false
        },
        series: [{
            name: 'Random data',
            data: (function () {
                // generate an array of random data
                var data = [],
                    time = (new Date()).getTime(),
                    i;

                for (i = -19; i <= 0; i += 1) {
                    data.push({
                        x: time + i * 1000,
                        y: Math.random()
                    });
                }

                return data;
            }())
        }]
    };

    return linechart;
}
*/