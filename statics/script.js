function process(data) {
    var result = JSON.parse(data)

    console.log(result);
    var refresh = parseFloat(result.refresh);
    var round = result.round;
    
    canvas = document.getElementById('canvas');
    ctx = canvas.getContext('2d');
    
    timeElement = document.getElementById('time');
    timeElement.innerHTML = "<pre>" + "Round: " + round + "</pre>" ;

    return refresh;
}

var latest_version = -1;

function ajax(version, retries, timeout) {
    console.log("Version " + version);
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

        console.log(refresh);
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

ajax(1, 10, 100);