// Holds the data structure for all the context menus used in the app
var server = 'http://47.100.40.223:9999/'

var script = document.createElement("script");
script.type = "text/javascript";
script.src = "jquery-3.5.1.min.js";
document.getElementsByTagName('head')[0].appendChild(script);

var script = document.createElement("script");
script.type = "text/javascript";
script.src = "https://unpkg.com/ulid@2.3.0/dist/index.umd.js";
document.getElementsByTagName('head')[0].appendChild(script);

function sendrequest(message, address) {
  var temp;
  $(function () {
    $.ajax({
      type: 'post',
      url: server + address,
      data: {
        url: message,
      },
      dataType: 'json',
      success: function (res) {
        // 返回成功的数据
        temp = res.message;
        if (res.code == 0) {
          new Notification(
            res.message, {
            body: " " + res.address,
            icon: 'http://images0.cnblogs.com/news_topic/firefox.gif',
            tag: { "aaa": "jansfbsad" } // 可以加一个tag
          }
          );
        }
        else {

          new Notification(
            res.message, {
            body: " " + res.address,
            icon: 'http://images0.cnblogs.com/news_topic/firefox.gif',
            tag: { "aaa": "jansfbsad" } // 可以加一个tag
          }
          );
        }
      }
    });
  });
}

function setUpContextMenus() {

  chrome.contextMenus.create({
    title: "添加",
    id: "add",
    //documentUrlPatterns: [ "chrome-extension://*/a.html","http://*/*", "https://*/*"],
    contexts: ['page'],
    onclick: function (info, tab) {
      // 注意不能使用location.href，因为location是属于background的window对象
      var current = ""
      current = tab.url;
      sendrequest(current, "add");
    }
  });
  chrome.contextMenus.create({
    title: "删除",
    id: "delete",
    //documentUrlPatterns: [ "chrome-extension://*/a.html","http://*/*", "https://*/*"],
    contexts: ['page'],
    onclick: function (info, tab) {
      // 注意不能使用location.href，因为location是属于background的window对象
      var current = ""
      current = tab.url;
      sendrequest(current, "delete");

    }
  });

}


function send_check_request(url) {
  $(function () {
    $.ajax({
      type: 'post',
      url: server + "check",
      data: {
        url: url,
      },
      dataType: 'json',
      success: function (res) {
        if (res.code == "2") {
          
          new Notification(
            "用户未登陆通知", {
            body: " ",
            icon: 'http://images0.cnblogs.com/news_topic/firefox.gif',

          })
        }
        //网页无效的时候发送通知消息
        else if (res.code == "1") {
          // 返回成功的数据
          
          new Notification(
            "网页无效通知", {
            body: " " + url,
            icon: 'http://images0.cnblogs.com/news_topic/firefox.gif',

          })

        }
        else {
          console.log("正常"+res.code)
        }
      }
    });
  });
}

//监听content.js传递的消息
chrome.runtime.onMessage.addListener(function (request, sender, sendResponse) {
  url = String(JSON.stringify(request).split("url:")[1]).replace('"','')
  msg = "收到content发送的url"
  console.log(msg + url)
  send_check_request(url)
  // new Notification(
  //   "title", {
  //   body: msg + " " + url,
  //   icon: 'http://images0.cnblogs.com/news_topic/firefox.gif',
  //   tag: { "aaa": "jansfbsad" } // 可以加一个tag
  // }
  // );
  sendResponse('我已收到你的消息：' + JSON.stringify(request));//做出回应
});


chrome.runtime.onInstalled.addListener(function () {
  // When the app gets installed, set up the context menus
  setUpContextMenus();
});


chrome.browserAction.onClicked.addListener(function (tab) {
  chrome.tabs.executeScript(tab.id, { file: 'https://unpkg.com/ulid@2.3.0/dist/index.umd.js' });
});
