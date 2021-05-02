// Holds the data structure for all the context menus used in the app
var server = 'http://localhost:8080/'

var script = document.createElement("script");
script.type = "text/javascript";
script.src = "https://cdn.bootcdn.net/ajax/libs/axios/0.21.0/axios.js";
document.getElementsByTagName('head')[0].appendChild(script);

var script = document.createElement("script");
script.type = "text/javascript";
script.src = "jquery-3.5.1.min.js";
document.getElementsByTagName('head')[0].appendChild(script);


var options = "0";
//https://developer.chrome.com/docs/extensions/reference/storage/

const user = { username: "", password: "" }

function a1() {

  var re = new Promise((resolve, reject) => {
    // Asynchronously fetch all data from storage.sync.
    chrome.storage.sync.get('key', (result) => {
      // Pass any observed errors down the promise chain.
      if (chrome.runtime.lastError) {
        return reject(chrome.runtime.lastError);
      }
      // Pass the data retrieved from storage down the promise chain.
      resolve(result.key);
      console.log("promise :" + result.key)
    });
  });
  return re;
}
function loginfunctionaxios(message) {
  var formdata = new FormData()
  var url = server + "checkuser"
  axios({
    method: "post",
    url: url,
    data: formdata,
    auth: {
      username: user.username,
      password: user.password,
    }
  }).then(response => {
    if (response.data.code == "200") {
      options = "1"
      console.log("验证账户成功")
      send_check_request(message);
    }

  })
}



async function compute(message) {
  var userinfo = await a1();

  console.log("compute :" + userinfo)

  if (userinfo != null&&userinfo!='' && userinfo.username != "") {
    Object.assign(user, { username: userinfo.split("@@@")[0], password: userinfo.split("@@@")[1] });
    if (options != "1") {
      loginfunctionaxios(message)

    }
    
  }
  else {
    new Notification(
      "用户未登陆",
      {
        body: " ",
        icon: 'http://images0.cnblogs.com/news_topic/firefox.gif',
        tag: { "aaa": "jansfbsad" } // 可以加一个tag
      }
    );
  }
}

function sendrequest(message, address) {
  var formdata = new FormData()
  formdata.append("url", message)
  var url = server + address
  axios({
    method: "post",
    url: url,
    data: formdata,
    auth: {
      username: user.username,
      password: user.password,
    }
  }).then(response => {

    if (response.data.code == "0") {
      new Notification(
        response.data.message, {
        body: " " + response.data.address,
        icon: 'http://images0.cnblogs.com/news_topic/firefox.gif',
        tag: { "aaa": "jansfbsad" } // 可以加一个tag
      }
      );
    }
    else {

      new Notification(
        response.data.message, {
        body: " " + response.data.address,
        icon: 'http://images0.cnblogs.com/news_topic/firefox.gif',
        tag: { "aaa": "jansfbsad" } // 可以加一个tag
      }
      );
    }

  })
}

function sendrequest2(message, address) {
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
//每次都自动加载右键菜单
setUpContextMenus()

function send_check_request(message) {
  var formdata = new FormData()
  formdata.append("url", message)
  var url = server + "check";

  axios({
    method: "post",
    url: url,
    data: formdata,
    auth: {
      username: user.username,
      password: user.password,
    }
  }).then(response => {
    
    
    response=JSON.parse(JSON.stringify(response))
    //此网页无效
    if (response.data.code == "1") {
      // 返回成功的数据
      console.log("response.data.code" + response.data.code)
      new Notification(
        response.data.message, {
        body: " " + message,
        icon: 'http://images0.cnblogs.com/news_topic/firefox.gif',

      })

    }
    //此网页有效
    else if (response.data.code == "0"){
      
      console.log("response.data.code" + response.data.code)
      // 返回成功的数据

      new Notification(
        response.data.message, {
        body: " " + message,
        icon: 'http://images0.cnblogs.com/news_topic/firefox.gif',

      })
    }

  })
}

function send_check_request23(url) {
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
          console.log("正常" + res.code)
        }
      }
    });
  });
}

//监听content.js传递的消息
chrome.runtime.onMessage.addListener(function (request, sender, sendResponse) {
  url = String(JSON.stringify(request).split("url:")[1]).replace('"', '')
  msg = "收到content发送的url"
  console.log(msg + url)
  if (options != '1') {
    compute(url);
  }
  else {
    send_check_request(url);
    console.log("check completed")
  }
  
  


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
  //setUpContextMenus();
});




