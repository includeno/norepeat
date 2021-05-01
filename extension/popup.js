
var server = 'http://47.100.40.223:9999/'

var vm = new Vue({
    el: '#app',//绑定方法1 绑定到具体的某个view
    data: {
        message: 'Welcome!',//html内数据绑定{{ message }}
        username: ``,
        checked: false,
        loginstate: false,
        logout_message: "登出",

        localName: "NoRepeat",
    },
    //页面加载完自动执行
    mounted: function () {
        console.log("mounted")

        //添加loaclstorage记录
        var name = window.localStorage.getItem(this.localName);
        console.log("loaclstorage 登陆" + name)
        if (name != null) {
            var formdata = new FormData()
            formdata.append("username", name)

            var url = server + "login"
            axios.post(url, formdata).then(function (response) {

                vm.loginstate = true;
                vm.username = response.data.username
                
                return false;
            })
        }

        var url = server + "checklogin"
        axios.get(url).then(function (response) {
            if (response.data.code == 0) {
                vm.loginstate = true;
                vm.username = response.data.username

            }
            else {
                vm.loginstate = false;
            }
        })
    },
    methods: {
        logout: function () {
            var url = server + "logout"
            axios.get(url).then(function (response) {
                vm.loginstate = false;
                window.localStorage.removeItem(vm.localName);//清除localStorage
            })
        },

    },
    components: {
        'vue_form': {
            template: `
            <div>
               <input type="text"  placeholder="请输入username" v-model="username">
               <input type="password" placeholder="请输入password">
               <button class="but" @click="login()">提交</button>
            </div>
            `,
            data: function () {
                return {
                    username: ""
                };
            },

            methods: {
                login: function () {
                    var formdata = new FormData()
                    formdata.append("username", this.username)

                    var url = server + "login"
                    axios.post(url, formdata).then(function (response) {

                        vm.loginstate = true;
                        vm.username = response.data.username
                        window.localStorage.setItem(vm.localName, vm.username);
                        return false;
                    })

                },
            },
        }
    },

});