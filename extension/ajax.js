var temp;
function sendrequest(message) {
    $(function () {
        $.ajax({
            type: 'post',
            url: 'http://127.0.0.1:5000/send',
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