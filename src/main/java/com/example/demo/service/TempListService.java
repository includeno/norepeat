package com.example.demo.service;

import com.google.gson.Gson;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class TempListService {
    Logger log= LoggerFactory.getLogger("TempListService");

    @Autowired
    FliterService fliterService;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    public String getKey(String username){
        String key = username + "::templist";
        return key;
    }

    public  Boolean addRecord(String username,String url,String time){
        String key=getKey(username);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat dayformat = new SimpleDateFormat("yyyy-MM-dd");
        Date inputtime=new Date();
        String inputtimestr="";
        try {
            inputtime= format.parse(time);
            inputtimestr = format.format(inputtime);
            System.out.println("inputtime:"+ inputtimestr);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        long expireOfKey = redisTemplate.getExpire(key);//-1永久  -2w无key
        System.out.println("expireOfKey:" + expireOfKey);
        url = fliterService.doFliterUrl(url);
        Boolean putResult = redisTemplate.opsForHash().putIfAbsent(key, url,inputtimestr);
        //首次
        if (putResult) {
            if (-2 == expireOfKey) {
                try {
                    Date today = dayformat.parse(time.split(" ")[0]);
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(today);
                    calendar.add(Calendar.DAY_OF_MONTH, 1);//利用Calendar 实现 Date日期+1天
                    Date tommorrow = calendar.getTime();
                    long timeoutUnit = tommorrow.getTime() - inputtime.getTime();    //相差毫秒数
                    redisTemplate.expire(key,timeoutUnit, TimeUnit.MILLISECONDS);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        return putResult;
    }
    public  Boolean addList(){
        return false;
    }

    public  ArrayList<HashMap<String, String>> getList(String username){

        ArrayList<HashMap<String, String>> result = new ArrayList<>();
        String key=getKey(username);
        Set<Object> keys = redisTemplate.opsForHash().keys(key);
        ArrayList<TempListRecord> records = new ArrayList<>();
        for (Object hashKey : keys) {
            String keystr = String.valueOf(hashKey);
            String timestr = String.valueOf(redisTemplate.opsForHash().get(key, hashKey));
            records.add(new TempListRecord(timestr, keystr));
        }
        records.sort((a1, a2) -> a1.time.compareTo(a2.time));
        for (TempListRecord record : records) {
            HashMap<String, String> map = new HashMap<>();
            log.info("key: " + record.time + " value:" + record.url);
            map.put("url", record.url);
            map.put("time", record.time);
            result.add(map);
        }
        return result;
    }


    //
    public  ArrayList<HashMap<String, String>> getListHashMap(String username){

        ArrayList<HashMap<String,String>> result = new ArrayList<>();
        String key=getKey(username);
        Set<Object> keys = redisTemplate.opsForHash().keys(key);
        ArrayList<TempListRecord> records = new ArrayList<>();
        Gson gson=new Gson();
        for (Object hashKey : keys) {
            String urlString = String.valueOf(hashKey);
            String timeString = String.valueOf(redisTemplate.opsForHash().get(key, hashKey));
            HashMap<String,String> entry=gson.fromJson(timeString,HashMap.class);
            
            records.add(new TempListRecord(urlString, timeString));
        }
        records.sort((a1, a2) -> a1.time.compareTo(a2.time));

        for (TempListRecord record : records) {
            HashMap<String, String> map = new HashMap<>();

            log.info("key: " + record.time + " value:" + record.url);
            map.put("url", record.url);
            map.put("time", record.time);
            result.add(map);
        }
        return result;
    }


    public  Boolean getRecord(){
        return false;
    }
    public  Boolean deleteRecord(String username,String url){
        String key = getKey(username);
        String hashkey=url;
        Long deleteResult =redisTemplate.opsForHash().delete(key,hashkey);
        if(deleteResult==null){
            return false;
        }
        else{
            return true;
        }

    }
    public  Boolean deleteList(String username){
        String key = getKey(username);
        Boolean deleteResult = redisTemplate.delete(key);
        return deleteResult;
    }


}

@Data
class TempListRecord {
    String url;
    String time;
    String comment;

    public TempListRecord(String url,String time ) {
        this.time = time;
        this.url = url;
        this.comment = "";
    }

    public TempListRecord(String url,String time,String comment ) {
        this.time = time;
        this.url = url;
        this.comment = comment;
    }
}