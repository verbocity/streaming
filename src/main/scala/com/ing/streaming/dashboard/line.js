/**
* A simple JavaScript image loaderimage loader
* @author Cuong Tham
* @url http://thecodecentral.com/2008/02/21/a-useful-javascript-image-loader
* @usage
* var loader = new ImageLoader('IMAGE_URL');
* //set event handler
* loader.loadEvent = function(url, image){
*   //action to perform when the image is loaded
*   document.body.appendChild(image);
* }
* loader.load();
*/

//source: http://snipplr.com/view.php?codeview&id=561
// Cross-browser implementation of element.addEventListener()
function addListener(element, type, expression, bubbling)
{
  bubbling = bubbling || false;
  if(window.addEventListener)	{ // Standard
    element.addEventListener(type, expression, bubbling);
    return true;
  } else if(window.attachEvent) { // IE
    element.attachEvent('on' + type, expression);
    return true;
  } else return false;
}

var ImageLoader = function(url){
  this.url = url;
  this.image = null;
  this.loadEvent = null;
};

ImageLoader.prototype = {
  load:function(){
    this.image = document.createElement('img');
    var url = this.url;
    var image = this.image;
    var loadEvent = this.loadEvent;
    addListener(this.image, 'load', function(e){
      if(loadEvent != null){
        loadEvent(url, image);
      }
    }, false);
    this.image.src = this.url;
  },
  getImage:function(){
    return this.image;
  }
};

function createCharts(jQuery) {
    Highcharts.setOptions({
        global: {
            useUTC: false
        }
    })

    if (!!window.EventSource) {
        var srcLine = new EventSource('http://localhost:8888/api/stream/line');
        var srcShops = new EventSource('http://localhost:8888/api/stream/shops');
        createLineChart(srcLine);
        createPieChart(srcLine);
        createImageTable(srcShops);
    } else {
      alert('no event source');
    }
}

function createLineChart(source) {
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
                         var chart = $('#container2').highcharts();

                         $.each(parsed, function (a, b) {
                            var id = -1

                            if (a == "less than €10") {
                                id = 0
                            } else if (a == "between €10 and €25") {
                                id = 1
                            } else if (a == "between €25 and €100") {
                                id = 2
                            } else if (a == "between €100 and €250") {
                                id = 3
                            } else if (a == "more than €250") {
                                id = 4
                            }

                            if (id >= 0) {
                                var x = (new Date()).getTime()
                                var y = parseInt(b);
                                chart.series[id].addPoint([x, y], true, true)
                            }
                         });
                     }, false);
                }
            }
        },
        series: [{
            name: 'Category 1',
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
        }, {
            name: 'Category 2',
            data: fillZeros()
        }, {
            name: 'Category 3',
            data: fillZeros()
        }, {
            name: 'Category 4',
            data: fillZeros()
        }, {
            name: 'Category 5',
            data: fillZeros()
        }],
        title: {
            text: 'Number of transactions per second'
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

function fillZeros() {
    // generate an array of random data
    var data = [],
      time = (new Date()).getTime(),
      i;

    for (i = -19; i <= 0; i += 1) {
      data.push({
          x: time + i * 1000,
          y: 0
      });
    }
    return data;
}

function createPieChart(source) {
    var data = []

    $('#container1').highcharts({
        chart: {
            type: 'pie',
            options3d: {
                enabled: true,
                alpha: 45,
                beta: 0
            },
            plotBackgroundColor: null,
            plotBorderWidth: 1,
            plotShadow: false,
            events: {
                 load: function() {
                     source.addEventListener('message', function(e) {
                         data = []
                         var parsed = $.parseJSON(e.data)

                         $.each(parsed, function (a, b) {
                            data.push([String(a), parseInt(b)]);
                         });

                         var chart = $('#container1').highcharts();
                         chart.series[0].setData(data);
                     }, false);
                 }
             }
        },
        title: {
            text: 'Spending categories'
        },
        tooltip: {
            pointFormat: '{series.name}: <b>{point.percentage:.1f}%</b>'
        },
        plotOptions: {
            pie: {
                allowPointSelect: true,
                cursor: 'pointer',
                dataLabels: {
                    enabled: false
                },
                depth: 35,
                showInLegend: true
            }
        }, series: [{
              type: 'pie',
              name: 'Spending category',
              data: data
        }]
    })
}

function createImageTable(source) {
    source.addEventListener('message', function(e) {
        data = []
        var parsed = $.parseJSON(e.data)

        $.each(parsed, function(a, b) {
            var loader = new ImageLoader(b["url"]);

            // 0: 1.1, 1: 1.2, 2: 1.3, 3: 1.4, 4: 1.5,
            // 5: 2.1, 6: 2.2, 7: 2.3, 8: 2.4, 9: 2.5
            var row = Math.floor((a / 5) + 1)
            var col = (a % 5) + 1

            loader.loadEvent = function(url, image) {
                var divid = '#r' + row + 'c' + col
                var img = document.createElement("img")

                var amount = parseInt(b["count"])

                //img.style.height = (50 * amount) + 'px'
                //img.style.width = (50 * amount) + 'px'
                img.width=50 * (amount / 20)
                img.height=50 * (amount / 20)

                img.src = b["url"]

               document.getElementById("r" + row + "c" + col).innerHTML = ""
               document.getElementById("r" + row + "c" + col).appendChild(img)
            }

            loader.load();
        });
    }, false);
}