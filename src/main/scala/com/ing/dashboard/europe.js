function createCharts(jQuery) {
    Highcharts.setOptions({
        global: {
            useUTC: false
        }
    })

    if (!!window.EventSource) {
        var srcEurope = new EventSource('http://localhost:8888/api/stream/europe');
        createEuropeMap(srcEurope);
    } else {
      alert('no event source');
    }
}

function createEuropeMap(source) {
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
                                'hc-key': b["hc-key"].toLowerCase(),
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
            max: 10000,
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