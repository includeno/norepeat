// Holds the data structure for all the context menus used in the app
var server = 'http://47.100.40.223:9999/'

var script = document.createElement("script");
script.type = "text/javascript";
script.src = "https://cdn.bootcdn.net/ajax/libs/axios/0.21.0/axios.js";
document.getElementsByTagName('head')[0].appendChild(script);

var script = document.createElement("script");
script.type = "text/javascript";
script.src = "jquery-3.5.1.min.js";
document.getElementsByTagName('head')[0].appendChild(script);


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

function send_check_request(message, username, password) {
  var formdata = new FormData()
  formdata.append("url", message)
  var url = server + "check";

  axios({
    method: "post",
    url: url,
    data: formdata,
    auth: {
      username: username,
      password: password,
    }
  }).then(response => {

    response = JSON.parse(JSON.stringify(response))
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
    else if (response.data.code == "0") {

      console.log("response.data.code" + response.data.code)
      // 返回成功的数据

      // new Notification(
      //   response.data.message, {
      //   body: " " + message,
      //   icon: 'http://images0.cnblogs.com/news_topic/firefox.gif',

      // })
    }

  })
}

function send_add_to_list_request(message, username, password) {
  var formdata = new FormData()
  formdata.append("url", message)
  var url = server + "addtemplist";

  axios({
    method: "post",
    url: url,
    data: formdata,
    auth: {
      username: username,
      password: password,
    }
  }).then(response => {

    response = JSON.parse(JSON.stringify(response))
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
    else if (response.data.code == "0") {

      console.log("response.data.code" + response.data.code)
      // 返回成功的数据

      // new Notification(
      //   response.data.message, {
      //   body: " " + message,
      //   icon: 'http://images0.cnblogs.com/news_topic/firefox.gif',

      // })
    }

  })
}

//以方法作为参数 https://www.cnblogs.com/kid526940065/p/8950654.html
async function compute(message,fn) {
  var userinfo = await a1();
  console.log("compute :" + userinfo)
  if (userinfo != null && userinfo != '' && userinfo.username != "") {
    Object.assign(user, { username: userinfo.split("@@@")[0], password: userinfo.split("@@@")[1] });
    let username = userinfo.split("@@@")[0];
    let password = userinfo.split("@@@")[1];

    if (options != "1") {
      
      var formdata = new FormData()
      var url = server + "checkuser"
      axios({
        method: "post",
        url: url,
        data: formdata,
        auth: {
          username: username,
          password: password,
        }
      }).then(response => {
        if (response.data.code == "200") {
          options = "1"
          console.log("验证账户成功")
          fn(message, username, password);
        }

      })
    }
    else {
      fn(message, username, password);
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


//监听content.js传递的消息
chrome.runtime.onMessage.addListener(function (request, sender, sendResponse) {
  if (request.startsWith("url:")) {
    url = String(JSON.stringify(request).split("url:")[1]).replace('"', '')
    msg = "收到content发送的url 用于判断网页是否重复"
    console.log(msg + url)
    compute(url,send_check_request);
    sendResponse('我已收到你的当前网页url：' + JSON.stringify(request));//做出回应
  }
  if (request.startsWith("templist:")) {
    url = String(JSON.stringify(request).split("templist:")[1]).replace('"', '')
    msg = "收到content发送的templist url 用于在用户列表添加网页"
    console.log(msg + url)
    sendResponse('我已收到你的添加列表请求url：' + JSON.stringify(request));//做出回应
    compute(url,send_add_to_list_request);
  }

});


chrome.runtime.onInstalled.addListener(function () {
  // When the app gets installed, set up the context menus
});




