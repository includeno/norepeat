
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


var url = "url:" + window.location.href
console.log('url send message', url);
chrome.runtime.sendMessage(url, (response) => {
    // 3. Got an asynchronous response with the data from the background
    console.log('received user data', response);
    
});


var currentelement = null;
const bingdingkey='192'
function tag_a_event_over(event) {
    //console.log('element=', event);
    //console.log('srcElement=', event.srcElement);
    //console.log('event.srcElement.href=',event.srcElement.href);
    //console.log('(event.srcElement)=>', (event.srcElement));
    if (event.srcElement!=null &&'href' in event.srcElement) {
        currentelement = event.srcElement.href;
        console.log('event.srcElement 1=>', event.srcElement.href);
    }
    else if (event.relatedTarget!=null &&'href' in event.relatedTarget) {
        currentelement = event.relatedTarget.href;
        console.log('event.relatedTarget 2=>', event.relatedTarget.href);
    }
    else if (event.target!=null &&'href' in event.target) {
        currentelement = event.target.href;
        console.log('event.target.href 3=>', event.target.href);
    }
    else if (event.target.parentElement!=null &&'href' in event.target.parentElement) {
        currentelement = event.target.parentElement.href;
        console.log('event.target.parentElement 4=>', event.target.parentElement.href);
    }
    
    else {
        //未找到
        console.log('未找到');
    }
    if (currentelement!=null &&currentelement.indexOf('javascript:')!=-1) {
        currentelement = null;
    }
    if (currentelement != null) {
        
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
    
    document.onkeyup = function (event) {
        var key = event.which;
        console.log("onkeyup Key: " + String.fromCharCode(key) + "\nCharacter code: " +String(key)  + " ");
        if (currentelement == null) {
            
        }
        else if (key == bingdingkey) {
            console.log('onkeyup key == ' + bingdingkey + ' 添加' + currentelement);
            let message="templist:"+currentelement
            chrome.runtime.sendMessage(message, (response) => {
                // 3. Got an asynchronous response with the data from the background
                console.log('收到', response);
                
            });
        }
        
    };
    addListenerOfTag();
}