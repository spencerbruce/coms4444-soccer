// import * from "https://cdn.anychart.com/releases/8.0.0/js/anychart-base.min.js"


function cumulativePoints(result) {

    // anychart.onDocumentReady(function() {
        // anychart.theme(anychart.themes.darkEarth);

        var cumulativePoints = result.cumulativePoints;
        var cumulativePointsData = {
            header: ["Team", "Cumulative Team Points"],
            rows: Object.entries(cumulativePoints)
        };

        // var chart = anychart.bar();
        // chart.data(cumulativePointsData);
        // chart.title("Cumulative Points");
        // chart.animation(true);
        // chart.tooltip().position('right-center');
        // chart.tooltip().background().fill("black");
        // chart.tooltip().title().fontColor("white");
        // chart.tooltip().fontColor("white");
        // chart.xAxis().title('Team')
        // chart.yAxis().title('Number of Points');
        
        // chart.hovered().labels(true);

        // chart.labels(true);
        // chart
        //     .labels()
        //     .fontColor("white")
        //     .format('{%Value}{groupsSeparator:}');

        // chart.container("cumulativeContainer1");
        // chart.draw();

        cumPointsElement = document.getElementById('cumulativePoints');
        console.log(cumPointsElement.innerHTML);
        cumPointsElement.innerHTML = cumulativePointsData.header;

    // });
}


function cumulativeResults(result) {
    // anychart.onDocumentReady(function() {
        // anychart.theme(anychart.themes.darkGlamour);

        var cumulativeWins = result.cumulativeWins;
        var cumulativeLosses = result.cumulativeLosses;
        var cumulativeDraws = result.cumulativeDraws;

        var teams = Object.keys(cumulativeWins);

        var cumulativeResultsData = []
        for(var i = 0; i < teams.length; i++) {
            var team = teams[i];
            
            teamCumulativeResults = [team]
            teamCumulativeResults.push(cumulativeWins[team]);
            teamCumulativeResults.push(cumulativeLosses[team]);
            teamCumulativeResults.push(cumulativeDraws[team]);

            cumulativeResultsData.push(teamCumulativeResults)
        }

        // var dataSet = anychart.data.set(cumulativeResultsData);

        // var winSeriesData = dataSet.mapAs({x: 0, value: 1});
        // var lossSeriesData = dataSet.mapAs({x: 0, value: 2});
        // var drawSeriesData = dataSet.mapAs({x: 0, value: 3});

        // var chart = anychart.column();
        // chart.title("Cumulative Win-Loss Record")
        // chart.animation(true);
        // chart.xAxis().title('Team')
        // chart.yAxis().title('Number of Games');

        // var setupSeries = function (series, name) {
        //     series.name(name);
        //     series.hovered().labels(false);

        //     series
        //         .labels()
        //         .enabled(true)
        //         .position('right-center')
        //         .anchor('left-center')
        //         .format('{%Value}{groupsSeparator:}');

        //     series
        //         .tooltip()
        //         .position('right')
        //         .anchor('left-center')
        //         .offsetX(5)
        //         .offsetY(0)
        //         .titleFormat('{%X}')
        //         .format('{%SeriesName}: {%Value}{groupsSeparator:}');
        // };

        // var series;
        // series = chart.column(winSeriesData);
        // setupSeries(series, 'Wins');
        // series = chart.column(lossSeriesData);
        // setupSeries(series, 'Losses');
        // series = chart.column(drawSeriesData);
        // setupSeries(series, 'Draws');

        // chart.legend().enabled(true).fontSize(13).padding([0, 0, 20, 0]);
        // chart.interactivity().hoverMode('single');
        // chart.tooltip().positionMode('point');

    // });

}

function cumulativeAverageRank(result) {

}

function roundGameGrid(result) {

}

function roundSummary(result) {

}


function process(data) {
    var result = JSON.parse(data)

    console.log(result);

    var refresh = parseFloat(result.refresh);
    var round = result.round;

    cumulativePoints(result)
    cumulativeResults(result)
    
    roundElement = document.getElementById('round');
    roundElement.innerHTML = "<pre>" + "Round: " + round + "</pre>";

    return refresh;
}

var latest_version = -1;

function ajax(version, retries, timeout) {
    var xhttp = new XMLHttpRequest();
    xhttp.onload = (function() {
        var refresh = -1;
        try {
            if(xhttp.readyState != 4)   
                throw "Incomplete HTTP request: " + xhttp.readyState;
            if(xhttp.status != 200)
                throw "Invalid HTTP status: " + xhttp.status;

            refresh = process(xhttp.responseText);
            if(latest_version < version)
                latest_version = version;
            else
                refresh = -1;
        } catch(message) {
            alert(message);
        }

        if(refresh >= 0)
            setTimeout(function() { ajax(version + 1, 10, 100); }, refresh);
    });
    xhttp.onabort = (function() { location.reload(true); });
    xhttp.onerror = (function() { location.reload(true); });
    xhttp.ontimeout = (function() {
        if(version <= latest_version)
            console.log("AJAX timeout (version " + version + " <= " + latest_version + ")");
        else if(retries == 0)
            location.reload(true);
        else {
            console.log("AJAX timeout (version " + version + ", retries: " + retries + ")");
            ajax(version, retries - 1, timeout * 2);
        }
    });
    xhttp.open("GET", "data.txt", true);
    xhttp.responseType = "text";
    xhttp.timeout = timeout;
    xhttp.send();
}

// function include(file) { 
  
//     var script = document.createElement('script'); 
//     script.src = file; 
//     script.type = 'text/javascript'; 
//     script.defer = true; 

//     document.getElementsByTagName('head').item(0).appendChild(script);  
// }

// include("https://cdn.anychart.com/releases/8.0.0/js/anychart-base.min.js")
// include("https://cdn.anychart.com/releases/8.0.0/themes/dark_earth.min.js")

ajax(1, 10, 100);