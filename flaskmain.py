# -*- coding: utf-8 -*-
# 导入Flask类
import os
from flask import jsonify
from flask import request
from flask_cors import CORS
from redis_operations import *
from flask import Flask, session, redirect, url_for, escape
import time

#存储被禁止的网页链接
#forbidden::userid::url zset
#whitelist::userid::url kv
#blacklist::userid:url kv
#setting::userid dict
#user::userid kv
class Data:
    currentuser = None
    
helper=RedisHelper(host="47.100.40.223",port=6379,db=0)
#实例化，可视为固定格式
app = Flask(__name__)
CORS(app, supports_credentials=True)
user=None

def init_user():
    global user
    if (Data.currentuser == None and 'username' in session):
        Data.currentuser = str(escape(session['username']))
        return True
    return False
#对URL进行简化处理
def simpleURL(link):
    #读取nosimple配置信息

    if(link.count("?")>0):
        link=link.split("?")[0]
        print("link需要简化"+str(link))
    else:
        print("link已经最简"+str(link))
    return link

#判断URL是否在 forbiddenSet中
def checkURL(user,link):
    link=simpleURL(link)
    status=helper.forbiden_get(user,link)
    if(status==None):#不存在
        return True
    else:#存在
        return False

#####用户登陆 登出操作
# set the secret key.  keep this really secret:SECRET_KEY 配置变量是通用密钥, 可在 Flask 和多个第三方扩展中使用
#如果app.secret_key未设置,则Flask将不允许你设置或访问会话字典。
app.secret_key = 'A0Zr98j/3yX R~XHH!jmN]LWX/,?RT'

@app.route('/checklogin',methods=['GET'])
def checklogin():
    
    
    ret = {'code': 1, 'message': "未登陆"}
    if 'username' in session:
        Data.currentuser=str(escape(session['username']))
        print("checklogin session "+Data.currentuser)
        ret = {'code': 0, 'message': "已登陆",'username':Data.currentuser}
    print(ret["message"])
    return jsonify(ret)


@app.route('/login', methods=['POST'])
def login():
    
    if 'username' in session:
        ret = {'code': 0, 'message': "已登陆",
               'username': escape(session['username'])}
        return jsonify(ret)
    else:
        if request.method == 'POST':
            Data.currentuser=request.form.get("username")
            print(Data.currentuser)
            session['username'] = Data.currentuser
            ret = {'code': 0, 'message': "已登陆",
                   'username': escape(session['username'])}
            print("Logged in succeed!")
            return jsonify(ret)

@app.route('/logout')
def logout():
    # remove the username from the session if it's there
    session.pop('username', None)
    
    Data.currentuser=None
    ret = {'code': 0, 'message': "已登出"}
    print("Logged out succeed!")
    return jsonify(ret)
#####业务

@app.route('/check', methods=["post"])
def check():
    link=request.form.get("url")
    link=simpleURL(link)
    print("current link",link)
    
    if not init_user():
        ret = {'code': 1, 'message': "此网页无效", 'address': link}
    ret = {'code': 1, 'message': "此网页无效",'address':link}
    if(checkURL(Data.currentuser,link)==True):
        ret = {'code': 0, 'message': "此网页有效",'address':link}
    return jsonify(ret)


@app.route('/add', methods=["post"])
def add():
    
    init_user()
    
    link=request.form.get("url")
    link=simpleURL(link)
    ret = {'code': 1, 'message': "已无效",'address':link}
    if(checkURL(Data.currentuser,link)==True):
        helper.forbiden_incr(Data.currentuser,link)
        ret = {'code': 0, 'message': "网页无效操作成功",'address':link}
        print("网页无效")
    return jsonify(ret)

@app.route('/delete', methods=["post"])
def delete():
    
    init_user()
    #user=request.form.get("user")
    link=request.form.get("url")
    link=simpleURL(link)
    print("url:"+str(link))
    ret = {'code': 0, 'message': "网页还原成功",'address':link}
    if(checkURL(user,link)==False):
        helper.forbiden_delete(user,link)
        print("网页还原")
    else:
        ret = {'code': 0, 'message': "网页未被禁止",'address':link}
    return jsonify(ret)

#上传文件 解析出文件内的link，保存至当前用户的redis数据库
@app.route('/upload', methods=["post"])
def upload():
    #forbiddenSet=readSet(localfile)
    ret = {'code': 0, 'message': "数据读取成功"}
    return jsonify(ret)

#https://www.cnblogs.com/yaoqingzhuan/p/10997201.html
#下载用户在redis中的所有数据,次数是不计算的
@app.route('/download', methods=["post"])
def download():
    #writeSet(localfile,forbiddenSet)
    ret = {'code': 0, 'message': "数据写入成功"}
    return jsonify(ret)

@app.route('/hello', methods=["get"])
def hello():
    print("hello world")
    return "hello world"

if __name__ == '__main__':
    # app.run(host, port, debug, options)
    # 默认值：host="127.0.0.1", port=5000, debug=False
    
    
    app.run(host="0.0.0.0", port=9999)
    print(user)
