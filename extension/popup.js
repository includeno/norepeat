
var server = 'http://47.100.40.223:9999/'
//https://segmentfault.com/q/1010000017010394

var vm = new Vue({
    el: '#app',
    data: {
        message: 'Welcome!',//html内数据绑定{{ message }}
        username: ``,
        checked: false,
        loginstate: false,
        logout_message: "登出/Logout",

        localName: "NoRepeat",

        templist: [],
    },
    //页面加载完自动执行
    mounted: function () {
        console.log("mounted")

        //尝试加载chrome.storage记录
        this.trylogin();
        
    },
    methods: {
        logout: function () {
            var url = server + "logout"
            axios({
                method: "post",
                url: url,
                auth: {
                    username: username,
                    password: password,
                }
            }).then(function (response) {
                vm.loginstate = false;
                chrome.storage.sync.set({ key: "" }, function () {
                    console.log('logout succeed');
                });
            })
        },
        trylogin: function () {
            var name = ""
            chrome.storage.sync.get(['key'], result => {
                
                name = result.key;

                if (name != null && name != "") {
                    username = name.split("@@@")[0]
                    password = name.split("@@@")[1]
                    
                    var url = server + "checkuser"
                    axios({
                        method: "post",
                        url: url,
                        auth: {
                            username: username,
                            password: password,
                        }
                    }).then(response => {
                        console.log('checkuser!');
                        if (response.data.code == "200") {
                            vm.loginstate = true;
                            vm.username = username
                            this.username = username
                            chrome.storage.sync.set({ key: username + "@@@" + password }, ()=> {
                                
                                this.gettemplist();
                            });
                            console.log('checkuser true!');
                            
                        }
                        else {
                            vm.loginstate = false;
                            chrome.storage.sync.set({ key: "" }, ()=> {
                                console.log('logout succeed');
                            });
                            console.log('checkuser false!');
                        }

                    })
                }
                else {

                }

            });
        },
        gettemplist: function () {
            var name = ""
            chrome.storage.sync.get(['key'], result => {
                name = result.key;

                if (name != null && name != "") {
                    username = name.split("@@@")[0]
                    password = name.split("@@@")[1]

                    var url = server + "gettemplist"
                    axios({
                        method: "get",
                        url: url,
                        auth: {
                            username: username,
                            password: password,
                        }
                    }).then(response => {
                        this.templist = []
                        for (var i = 0; i < response.data.length; i++) {
                            this.templist.push(response.data[i]);
                        }
                    });
                }

            });
        },
        opennewtab: function (url) {
            chrome.tabs.create({ url: url }, function (tab) {
            });

        },
        show_standard_url: function (url) {
            if (url.length<=60) {
                return url;
            }
            else {
                return url.substring(0,54)+"......"
            }
        },
    },
    components: {
        'vue_form': {
            template: `
            <div>
               <input type="text"  placeholder="请输入username" v-model="username">
               <input type="password" placeholder="请输入password" v-model="password">
               <button class="but" @click="register()">注册/Register</button>
               <button class="but" @click="login()">登陆/Login</button>
            </div>
            `,
            data: function () {
                return {
                    username: "",
                    password: "",
                };
            },

            methods: {
                login: function () {
                    var formdata = new FormData()
                    var url = server + "checkuser"
                    axios({
                        method: "post",
                        url: url,
                        data: formdata,
                        auth: {
                            username: this.username,
                            password: this.password,
                        }
                    }).then(response => {
                        console.log("response.data:" + response.data)
                        if (response.data.code == '200') {
                            vm.loginstate = true;
                            vm.username = this.username
                            console.log("response.data.code:" + response.data.code)
                            chrome.storage.sync.set({ key: this.username + "@@@" + this.password }, function () {
                                console.log('Value is set to ' + this.username + "@@@" + this.password);
                                vm.trylogin()
                            });
                        }
                        else {
                            console.log("response.data.code:" + response.data.code)
                            alert("登陆失败/Login fail")

                        }

                    })

                },
                register: function () {
                    var formdata = new FormData()
                    formdata.append("username", this.username)
                    formdata.append("password", this.password)
                    var url = server + "register"
                    axios.post(url, formdata).then(response => {
                        if (response.data.code == '200') {
                            alert("注册成功/Registering succeeds")
                        }
                        else {
                            alert("注册失败用户名已存在/Registering fails")
                        }
                    })

                },
            },
        }
    },

});