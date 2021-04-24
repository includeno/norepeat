//var server = 'http://47.100.40.223:9999/'
var server = 'http://127.0.0.1:9999/'



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