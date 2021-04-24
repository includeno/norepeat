var server = 'http://127.0.0.1:9999/'


var temp;
function sendrequest(message) {
    $(function () {
        $.ajax({
            type: 'post',
            url: server+'send',
            data:{
                id:message
            },
            success: function (res) {
                // 返回成功的数据
                temp = res.data;
                if (res.code == 0) {
                    
                }
            }
        });
    });
}