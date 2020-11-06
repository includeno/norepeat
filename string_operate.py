# -*- coding: utf-8 -*-
import os

#首字母大写
def firstLetterUpper(name):
    s1 = list(name)
    for ch in range(len(s1)):
        if (ch==0):
            s1[ch] = s1[ch].upper()
            break
    result = ''.join(s1)
    return result

# 去除数据库的下划线 加上大写字母
def getJavaColumnname(columnname):
    s1 = list(columnname)
    if (columnname.count('_') > 0):
        for ch in range(len(s1)):
            if (s1[ch] == '_'):
                s1[ch] = ''
                s1[ch + 1] = s1[ch + 1].upper()
    result = ''.join(s1)
    return result

#去除字符串换行符
def remove_n(inputline):
    s1 = list(inputline)
    result = ''
    for ch in range(len(s1)):
        if (s1[ch] == '\n'):
            s1[ch] = ""
        result = result+s1[ch]
    return result

# 利用切片操作，实现一个trim()函数，去除字符串首尾的空格，注意不要调用str的strip()方法
def trim(s):
    while s[0:1] == ' ':
        s = s[1:]
    while s[(len(s)-1):len(s)] == ' ':
        s = s[:-1]
    return s


# 格式化传入的setting 字符串 去掉\n 去掉首尾空格
def format_str(tempstr):
    if (tempstr == "\n"):
        tempstr = ""
    else:
        tempstr = remove_n(tempstr)#去掉\n
        tempstr=trim(tempstr)#去掉首尾空格
    return tempstr