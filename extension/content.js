//var server = 'http://47.100.40.223:5000/'
var server = 'http://127.0.0.1:9999/'

//废除
function get_user() {
    var id = ULID.ulid()
    return id.substring(0, 6)
}

function checkNotification() {
    if (!("Notification" in window)) {
        alert("This browser does not support desktop notification");
    }
    // check whether notification permissions have alredy been granted
    // Otherwise, ask the user for permission
    else if (Notification.permission !== 'denied') {
        Notification.requestPermission(function (permission) {
            // If the user accepts, let's create a notification
            if (permission === "granted") {
                //new Notification("Request granted!");
            }
        });
    }
}
function sendrequest(message, address) {
    var current = window.location.href;
    var temp;
    $(function () {
        $.ajax({
            type: 'post',
            url: server + address,
            data: {
                url: current,
            },
            dataType: 'json',
            success: function (res) {
                if (res.code == 2) {
                    res.message = "未登陆";
                    res.address = "";
                    chrome.runtime.sendMessage(res, function (response) {

                    });
                }
                //网页无效的时候发送通知消息
                else if (res.code == 1) {
                    // 返回成功的数据
                    res.address = current;

                    chrome.runtime.sendMessage(res, function (response) {

                    });
                }
                /**else {
                    new Notification(
                        "title",{
                            body :res.data,
                            icon : 'http://images0.cnblogs.com/news_topic/firefox.gif',
                            tag : {} // 可以加一个tag
                        }
                    );
                } */
            }
        });
    });
}

function clickbinding() {
    $("#reada").on('click', function () {
        sendrequest("", "read");
    });

    $("#writea").on('click', function () {
        sendrequest("", "write");
    });
}



sendrequest("", "check");
clickbinding();