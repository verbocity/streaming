function createCharts(jQuery) {
    Highcharts.setOptions({
        global: {
            useUTC: false
        }
    })

    if (!!window.EventSource) {
        var srcWorld = new EventSource('http://localhost:8888/api/stream/world');
        createWorldMap(srcWorld);
    } else {
      alert('no event source');
    }
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
            max: 100,
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