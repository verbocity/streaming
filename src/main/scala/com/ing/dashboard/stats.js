function createCharts(jQuery) {
    Highcharts.setOptions({
        global: {
            useUTC: false
        }
    })

    if (!!window.EventSource) {
        var srcStats = new EventSource('http://localhost:8888/api/stream/stats');
        updateStatistics(srcStats);
    } else {
      alert('no event source');
    }
}

function updateStatistics(source) {
    source.addEventListener('message', function(e) {
        parsed = $.parseJSON(e.data)

        var time = parsed["currentTime"]
        $('#currentTime').text(time)

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