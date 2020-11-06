# -*- coding: utf-8 -*-
# 导入Flask类
import os
from flask import Flask, jsonify
from flask import request
from flask_cors import CORS
from file_to_data import *


#存储被禁止的网页链接
forbiddenSet = {}
whitelist = []
blacklist = []
nosimple = []
localfile="C:\\data\\extension\\norepeat\\data.txt"
backup="C:\\data\\extension\\norepeat\\backup.txt"


#实例化，可视为固定格式
app = Flask(__name__)
CORS(app, supports_credentials=True)

#对URL进行简化处理
def simpleURL(link):
    #读取nosimple配置信息
    #TODO

    if(link.count("?")>0):
        link=link.split("?")[0]
        print("link需要简化"+str(link))
    else:
        print("link已经最简"+str(link))
    return link

#判断URL是否在 forbiddenSet中
def checkURL(link):
    link=simpleURL(link)
    status=forbiddenSet.get(link)
    if(status==None):#不存在
        print("1")
        return 1
    else:#存在
        print("2")
        return 2
    


@app.route('/check', methods=["post"])
def check():
    name=request.form.get("url")
    name=simpleURL(name)
    print("url:"+str(name))
    result=checkURL(name)
    ret = {'code': 1, 'data': "此网页无效"}
    if(result==True):
        ret = {'code': 0, 'data': "此网页有效"}
    return jsonify(ret) 


@app.route('/add', methods=["post"])
def add():
    name=request.form.get("url")
    name=simpleURL(name)
    print("url:"+str(name))
    ret = {'code': 1, 'data': "操作不成功"}
    if(checkURL(name)==1):
        forbiddenSet[name]=True
        writeSet(localfile,forbiddenSet)
        ret = {'code': 0, 'data': "网页无效操作成功"}
        print("网页无效")
    return jsonify(ret)

@app.route('/delete', methods=["post"])
def delete():
    name=request.form.get("url")
    name=simpleURL(name)
    print("url:"+str(name))
    ret = {'code': 0, 'data': "网页还原成功"}
    if(checkURL(name)==2):
        forbiddenSet.pop(name)
        writeSet(localfile,forbiddenSet)
        print("网页还原")
    else:
        ret = {'code': 0, 'data': "网页未被禁止"}
    return jsonify(ret)

@app.route('/read', methods=["post"])
def read():
    forbiddenSet=readSet(localfile)
    ret = {'code': 0, 'data': "数据读取成功"}
    return jsonify(ret)

@app.route('/write', methods=["post"])
def write():
    writeSet(localfile,forbiddenSet)
    ret = {'code': 0, 'data': "数据写入成功"}
    return jsonify(ret)

if __name__ == '__main__':
    # app.run(host, port, debug, options)
    # 默认值：host="127.0.0.1", port=5000, debug=False
    forbiddenSet = readSet(localfile)
    blacklist=readList("C:\\DataPosition\\GitHub_Projects\\norepeat\\black.txt")
    whitelist = readList("C:\\DataPosition\\GitHub_Projects\\norepeat\\white.txt")
    nosimple = readList("C:\\DataPosition\\GitHub_Projects\\norepeat\\nosimple.txt")
    print(nosimple)
    app.run(host="127.0.0.1", port=5000)
