var server = 'http://47.100.40.223:5000/'
function get_user() {
    var id = ULID.ulid()
    return id.substring(0,6)
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
                user:get_user()
            },
            dataType: 'json',
            success: function (res) {
                // 返回成功的数据
                temp = res.data;
                if (res.code == 0) {
                    
                    new Notification(
                        "title",{
                            body :res.data,
                            icon : 'http://images0.cnblogs.com/news_topic/firefox.gif',
                            tag : {} // 可以加一个tag
                        }
                    );
                    console.log("" + res.data)
                }
                else {
                    new Notification(
                        "title",{
                            body :res.data,
                            icon : 'http://images0.cnblogs.com/news_topic/firefox.gif',
                            tag : {} // 可以加一个tag
                        }
                    );
                }
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