
function sendrequest(message,address) {
    var current = window.location.href;
    var temp;
    $(function () {
        $.ajax({
            type: 'post',
            url: 'http://127.0.0.1:5000/'+address,
            data: {
                url: current
            },
            dataType: 'json',
            success: function (res) {
                // 返回成功的数据
                temp = res.data;
                if (res.code == 0) {
                    //alert(temp);
                    console.log(""+res.data)
                }
                else {
                    alert(temp);
                }
            }
        });
    });
}

function clickbinding() {
    $("#reada").on('click', function () {
        sendrequest("","read");
    });

    $("#writea").on('click', function () {
        sendrequest("","write");
    });
}



sendrequest("", "check");
clickbinding();