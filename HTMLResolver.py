# -*- coding: utf-8 -*-
import os
from requests_html import HTMLSession


url = 'https://www.jianshu.com/p/85f4624485b9'
#https://blog.csdn.net/silmeweed/article/details/101560289
url = 'https://blog.csdn.net/silmeweed/article/details/101560289'
url = 'https://blog.csdn.net/m0_38031406/article/details/88844190'

#区分csdn原创非原创
def csdn_resolver(url):
    session = HTMLSession()
    result= session.get(url)
    about = result.html.find('.article-type-img', first=True)
    print(about.attrs.get("src"))
    pic = about.attrs.get("src")
    if (str(pic).count("original.png") > 0):
        print("原创")
    else:
        print("非原创")
csdn_resolver(url)