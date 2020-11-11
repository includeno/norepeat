# -*- coding: utf-8 -*-
# 导入Flask类
import os
from flask import Flask, jsonify
from flask import request
from flask_cors import CORS
from redis_operations import *


#存储被禁止的网页链接
#forbidden::userid::url zset
#whitelist::userid::url kv
#blacklist::userid:url kv
#setting::userid dict
#user::userid kv

helper=RedisHelper(host="47.100.40.223",port=6379,db=0)
#实例化，可视为固定格式
app = Flask(__name__)
CORS(app, supports_credentials=True)

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
    
@app.route('/check', methods=["post"])
def check():
    user = request.form.get("user")
    link=request.form.get("url")
    print("user", user)
    print("link",link)
    
    link=simpleURL(link)
    ret = {'code': 1, 'data': "此网页无效"}
    if(checkURL(user,link)==True):
        ret = {'code': 0, 'data': "此网页有效"}
    return jsonify(ret) 


@app.route('/add', methods=["post"])
def add():
    user=request.form.get("user")
    link=request.form.get("url")
    link=simpleURL(link)
    ret = {'code': 1, 'data': "操作不成功"}
    if(checkURL(user,link)==True):
        helper.forbiden_incr(user,link)
        ret = {'code': 0, 'data': "网页无效操作成功"}
        print("网页无效")
    return jsonify(ret)

@app.route('/delete', methods=["post"])
def delete():
    user=request.form.get("user")
    link=request.form.get("url")
    link=simpleURL(link)
    print("url:"+str(link))
    ret = {'code': 0, 'data': "网页还原成功"}
    if(checkURL(user,link)==False):
        helper.forbiden_delete(user,link)
        print("网页还原")
    else:
        ret = {'code': 0, 'data': "网页未被禁止"}
    return jsonify(ret)

@app.route('/read', methods=["post"])
def read():
    #forbiddenSet=readSet(localfile)
    ret = {'code': 0, 'data': "数据读取成功"}
    return jsonify(ret)

@app.route('/write', methods=["post"])
def write():
    #writeSet(localfile,forbiddenSet)
    ret = {'code': 0, 'data': "数据写入成功"}
    return jsonify(ret)

@app.route('/hello', methods=["get"])
def hello():
    print("hello world")

if __name__ == '__main__':
    # app.run(host, port, debug, options)
    # 默认值：host="127.0.0.1", port=5000, debug=False
    
    
    app.run(host="0.0.0.0", port=5000)
