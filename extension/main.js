// Holds the data structure for all the context menus used in the app
var server = 'http://47.100.40.223:5000/'
//var server='http://127.0.0.1:5000/'
var script = document.createElement("script");
script.type = "text/javascript";
script.src = "jquery-3.5.1.min.js";
document.getElementsByTagName('head')[0].appendChild(script);

var script = document.createElement("script");
script.type = "text/javascript";
script.src = "https://unpkg.com/ulid@2.3.0/dist/index.umd.js";
document.getElementsByTagName('head')[0].appendChild(script);
var CONTEXT_MENU_CONTENTS = {
  forWindows: [
    'foo',
    'bar',
    'baz'
  ],
  forSelection: [
    'Selection context menu'
  ],
  forLauncher: [
    'Launch Window "A"',
    'Launch Window "B"'
  ]
}
function get_user() {
  var id = ULID.ulid()
  return id.substring(0,6)
}

var temp;
function sendrequest(message,address) {
  $(function () {
    $.ajax({
      type: 'post',
      url: server+address,
      data: {
        url: message,
        user:get_user()
      },
      dataType: 'json',
      success: function (res) {
        // 返回成功的数据
        temp = res.data;
        if (res.code == 0) {
          alert(temp);
        }
        else {
          alert("当前页面无效");
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
      var current = "aa"
      current = tab.url;
      sendrequest(current,"add");
    }
  });
  chrome.contextMenus.create({
    title: "删除",
    id: "delete",
    //documentUrlPatterns: [ "chrome-extension://*/a.html","http://*/*", "https://*/*"],
    contexts: ['page'],
    onclick: function (info, tab) {
      // 注意不能使用location.href，因为location是属于background的window对象
      var current = "aa"
      current = tab.url;
      sendrequest(current,"delete");

    }
  });

}


chrome.runtime.onInstalled.addListener(function () {
  // When the app gets installed, set up the context menus
  setUpContextMenus();
});

chrome.contextMenus.onClicked.addListener(function (itemData) {

});
chrome.browserAction.onClicked.addListener(function(tab) {
  chrome.tabs.executeScript(tab.id, {file: 'https://unpkg.com/ulid@2.3.0/dist/index.umd.js'});
});
chrome.tabs.onUpdated.addListener(function(tabId, info, tab) {
  if (tab.url !== undefined && info.status == "complete") {

    
  }
  
});