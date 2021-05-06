
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



var box = null;



var url = "url:" + window.location.href
console.log('url send message', url);
chrome.runtime.sendMessage(url, (response) => {
    // 3. Got an asynchronous response with the data from the background
    console.log('received user data', response);

});

function elementpos(event) {
    var screen_width = screen.availWidth;
    var screen_height = screen.availHeight;
    var currentmouse = document.elementFromPoint(event.screenX, event.screenY);
    // box.style.top = ''  + event.screenY + 'px'
    // box.style.left = '' +event.screenX + 'px'
    var y = (parseInt(event.pageY));
    var x = (parseInt(event.pageX));
    if (y > 0 && y <= screen_height - 40) {
        box.style.top = '' + (y + 30).toString() + 'px';
    }
    if (y > screen_height) {
        box.style.top = '' + (y - 40).toString() + 'px';
        console.log("(parseInt(event.pageY) - 50).toString()" + (y - 70).toString());
    }


    if (x > 0 && x <= screen_width - 40) {
        box.style.left = '' + (x + 30).toString() + 'px';

    }
    if (x > screen_width) {
        box.style.left = '' + (x - 70).toString() + 'px';
        console.log("(parseInt(event.pageX) - 50).toString()" + (x - 70).toString())
    }
    box.style.position = 'absolute';


    if (currentelement != null && currentelement != '') {
        box.innerHTML = '<h2>添加至列表/Add to list</h2>'
        box.style.color = "red"
    }
    else {
        box.innerHTML = ''
    }

    // if (currentmouse != null) {
    //     //console.log('currentmouse', currentmouse.tagName);
    //     if (currentmouse.tagName == 'a') {
    //         //console.log('currentmouse =>a', currentmouse.href);
    //     }
    // }

}

var currentelement = null;
const bingdingkey = '192'

//测试案例1 谷歌商店 https://chrome.google.com/webstore/search/url?_category=extensions
//测试案例2 百度收藏夹 通过
//测试案例3 百度搜索结果 通过
function tag_a_event_over(event) {
    console.log('element=', event);
    //console.log('srcElement=', event.srcElement);
    //console.log('event.srcElement.href=',event.srcElement.href);
    //console.log('(event.srcElement)=>', (event.srcElement));
    if (event.srcElement != null && 'href' in event.srcElement) {
        currentelement = event.srcElement.href;
        console.log('event.srcElement 1=>', event.srcElement.href);
    }
    else if (event.relatedTarget != null && 'href' in event.relatedTarget) {
        currentelement = event.relatedTarget.href;
        console.log('event.relatedTarget 2=>', event.relatedTarget.href);
    }
    else if (event.target != null && 'href' in event.target) {
        currentelement = event.target.href;
        console.log('event.target.href 3=>', event.target.href);
    }
    else if (event.target.parentElement != null && 'href' in event.target.parentElement) {
        currentelement = event.target.parentElement.href;
        console.log('event.target.parentElement 4=>', event.target.parentElement.href);
    }
    else {
        //未找到
        console.log('未找到');
    }
    if (currentelement == null) {
        temp = null;
        ele = event.srcElement;
        while (ele != null) {
            ele = ele.parentElement;
            if (ele != null && ele.tagName == 'a') {
                temp = ele;
            }
        }
        if (temp != null) {
            console.log('temp.href 5=>', temp.href);
            currentelement = temp.href;
        }
    }

    if (currentelement != null && currentelement.indexOf('javascript:') != -1) {
        currentelement = null;
    }
    if (currentelement != null) {
        currentelement = currentelement.replace(" ", "");
    }
}

function tag_a_event_out(event) {
    currentelement = null;
}

function addListenerOfTag() {
    var linkTags = document.getElementsByTagName("a");
    for (var i = 0; i < linkTags.length; i++) {

        //添加onmouseover事件
        linkTags[i].onmouseover = tag_a_event_over;
        linkTags[i].onmouseout = tag_a_event_out;
    }
}
window.onload = function () {
    box = document.createElement('div');
    box.innerHTML = '<h1>cccdsddsd</h1>'
    box.id = 'boxmy'
    document.body.appendChild(box);

    document.onkeyup = function (event) {
        var key = event.which;
        console.log("onkeyup Key: " + String.fromCharCode(key) + "\nCharacter code: " + String(key) + " ");
        if (currentelement == null) {

        }
        else if (key == bingdingkey && currentelement != '' && (currentelement.startsWith("http") || currentelement.startsWith("https"))) {
            console.log('onkeyup key == ' + bingdingkey + ' 添加' + currentelement);
            var message = "templist:" + currentelement
            chrome.runtime.sendMessage(message, (response) => {
                // 3. Got an asynchronous response with the data from the background
                console.log('收到', response);

            });
        }

    };
    addListenerOfTag();
    document.onmouseover = elementpos;
}