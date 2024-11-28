if (window.location.href.indexOf("logout") > -1) {
    setTimeout(openNav, 1200);

}

function openNav() {
    document.getElementById('formLogin').style.display = 'none'
    document.getElementById('logoutmsg').style.display = 'none'
    getTime();
    document.getElementById("myNav").style.width = "100%";
}

function closeNav() {
    document.getElementById('formLogin').style.display = 'block'
    document.getElementById("myNav").style.width = "0%";
}


function getTime() {
    var today = new Date();
    var h = today.getHours();
    var m = today.getMinutes();
    var s = today.getSeconds();
    // add a zero in front of numbers<10
    m = checkTime(m);
    s = checkTime(s);
    document.getElementById('showtime').innerHTML = h + ":" + m + ":" + s;
    t = setTimeout(function () {
        getTime()
    }, 500);
}

function checkTime(i) {
    if (i < 10) {
        i = "0" + i;
    }
    return i;
}