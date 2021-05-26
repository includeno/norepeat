
var server = 'http://127.0.0.1:9999/'
//https://segmentfault.com/q/1010000017010394

var vm = new Vue({
    el: '#app',
    data: {
        message: 'Welcome!',//html内数据绑定{{ message }}
        username: ``,
        checked: false,
        loginstate: '0',//0未登陆 1已登陆 2注册 3重置密码
        logout_message: "登出/Logout",
        localName: "NoRepeat",

        templist: [],

        registerstate:false,
        revertstate:false,
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
        deletetemplist: function () {
            var name = ""
            chrome.storage.sync.get(['key'], result => {
                name = result.key;

                if (name != null && name != "") {
                    username = name.split("@@@")[0]
                    password = name.split("@@@")[1]

                    var url = server + "deletetemplist"
                    axios({
                        method: "post",
                        url: url,
                        auth: {
                            username: username,
                            password: password,
                        }
                    }).then(response => {
                        this.templist = []
                        
                    });
                }

            });
        },
        deletetemplistitem:function(index){
            let keyurl=this.templist[index].url;
            var formdata = new FormData()
            formdata.append("url", keyurl)
            
            
            chrome.storage.sync.get(['key'], result => {
                let name = result.key;

                if (name != null && name != "") {
                    username = name.split("@@@")[0]
                    password = name.split("@@@")[1]

                    var url = server + "deletetemplistitem"
                    axios({
                        method: "post",
                        url: url,
                        data:formdata,
                        auth: {
                            username: username,
                            password: password,
                        }
                    }).then(response => {
                        
                        this.templist.splice(index, 1);
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
        'vue_form_login': {
            template: `
            <div>
               <input type="text"  placeholder="请输入username" v-model="username">
               <input type="password" placeholder="请输入password" v-model="password">
               <button class="but" @click="login()">登陆/Login</button>

               <button class="but" @click="register()">注册/Register</button>
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
        

        ,
        'vue_form_register': {
            template: `
            <div>
               <input type="text"  placeholder="请输入username" v-model="username">
               <input type="password" placeholder="请输入password" v-model="password">
               <button class="co" @click="sendcode()">发送验证码/send code</button>
               <input type="text"  placeholder="请输入验证码" v-model="code" v-if="codestate">
               <button class="but" @click="register()" v-if="codestate">注册/Register</button>

            </div>
            `,
            data: function () {
                return {
                    username: "",
                    password: "",
                    code:"",
                    codestate:false,
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
                reset:function(){
                    
                },
                sendcode:function(){

                },
            },
        },
        'vue_form_reset': {
            template: `
            <div>
               <input type="text"  placeholder="请输入username" v-model="username">
               <input type="password" placeholder="请输入password" v-model="password">
               <input type="text"  placeholder="请输入验证码" v-model="code">
               <button class="co" @click="sendcode()">发送验证码/send code</button>
               <button class="but" @click="reset()">重置/Reset</button>
            </div>
            `,
            data: function () {
                return {
                    username: "",
                    password: "",
                    code:"",
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
                reset:function(){

                },
                sendcode:function(){

                },
            },
        },

    },

});