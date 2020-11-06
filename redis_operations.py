#https://www.runoob.com/w3cnote/python-redis-intro.html


#pip install redis  #3.5.3
import redis
import re

#用于验证ip
def check_ip(ipAddress):
  compile_ip=re.compile(r'^(1\d{2}|2[0-4]\d|25[0-5]|[1-9]\d|[1-9])\.(1\d{2}|2[0-4]\d|25[0-5]|[1-9]\d|\d)\.(1\d{2}|2[0-4]\d|25[0-5]|[1-9]\d|\d)\.(1\d{2}|2[0-4]\d|25[0-5]|[1-9]\d|\d)$')
  if compile_ip.match(ipAddress):
    return True
  else:
    return False

# https://www.cnblogs.com/hellojackyleon/p/9822006.html
def singleton(cls):
    
    instances = {}
    def _singleton(*args, **kwargs):
        if cls not in instances:
            instances[cls] = cls(*args, **kwargs)
        return instances[cls]
    return _singleton

@singleton
class RedisHelper:
    """
    RedisHelper
    """
    host = "47.100.40.223"
    port = 6379
    db = 0

    pool=None
    connection = None

    #默认是固定格式db0  8583是db1 批处理是db2
    def __init__(self,host,port,db=0):
        """
        host默认是"47.100.40.223"
        """
        if(host=="localhost" or check_ip(host)):
            self.host = host
        else:
            raise Exception("ip不合法")
        if (isinstance(port, int) and port > 0):
            self.port=port
        self.db = db
        self.pool = redis.ConnectionPool(host=self.host, port=self.port, db=self.db,decode_responses=True)
        print("init")

    
    def add(self, key, value):
        """
        只用于新增数据
        """
        if (self.connection == None):
            self.connection = redis.StrictRedis(connection_pool=self.pool)
        self.connection.set(key, value, nx=True)
        print("add")
        self.pool.disconnect()
        self.connection = None

    def get(self, key):
        """
        只用于查询数据
        """
        if (self.connection == None):
            self.connection = redis.StrictRedis(connection_pool=self.pool)
        result = self.connection.get(key)
        self.pool.disconnect()
        self.connection = None
        return result
    
    def forbiden_incr(self,user, url):
        """
        只用于查询数据
        """
        if (self.connection == None):
            self.connection = redis.StrictRedis(connection_pool=self.pool)
        key="forbidden::"+str(user)+"::"+str(url)
        result=self.connection.incr(key)
        self.pool.disconnect()
        self.connection = None
        return result
    def forbiden_get(self,user, url):
        """
        只用于查询数据
        """
        if (self.connection == None):
            self.connection = redis.StrictRedis(connection_pool=self.pool)
        key="forbidden::"+str(user)+"::"+str(url)
        result=self.connection.get(key)
        self.pool.disconnect()
        self.connection = None
        return result
    def forbiden_delete(self,user, url):
        """
        只用于查询数据
        """
        if (self.connection == None):
            self.connection = redis.StrictRedis(connection_pool=self.pool)
        key="forbidden::"+str(user)+"::"+str(url)
        self.connection.delete(key)
        self.pool.disconnect()
        self.connection = None
