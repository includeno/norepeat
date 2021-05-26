package com.example.demo.service;

import com.google.gson.Gson;
import lombok.AllArgsConstructor;
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
public class TempListHashMapService {
    Logger log= LoggerFactory.getLogger("TempListHashMapService");

    @Autowired
    FliterService fliterService;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    public String getKey(String username){
        String key = username + ":templist";
        return key;
    }

    public  Boolean addRecord(String username,String url,String time){
        String key=getKey(username);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat dayformat = new SimpleDateFormat("yyyy-MM-dd");
        Date inputtime=new Date();
        String timeString="";
        try {
            inputtime= format.parse(time);
            timeString = format.format(inputtime);
            System.out.println("timeString:"+ timeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long expireOfKey = redisTemplate.getExpire(key);//-1永久  -2w无key
        System.out.println("expireOfKey:" + expireOfKey);
        url = fliterService.doFliterUrl(url);
        Gson gson=new Gson();
        HashMap<String,String> entry=new HashMap<>();
        entry.put("comment","");
        entry.put("color","black");
        entry.put("visited","0");
        String value=gson.toJson(entry);
        Boolean putResult = redisTemplate.opsForHash().putIfAbsent(key, url,value);
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


    public  ArrayList<HashMap<String, String>> getListHashMap(String username){

        ArrayList<HashMap<String,String>> result = new ArrayList<>();
        String key=getKey(username);
        Set<Object> keys = redisTemplate.opsForHash().keys(key);
        ArrayList<TempListHashMapServiceRecord> records = new ArrayList<>();
        Gson gson=new Gson();
        for (Object hashKey : keys) {
            String urlString = String.valueOf(hashKey);
            String entryString = String.valueOf(redisTemplate.opsForHash().get(key, hashKey));
            HashMap<String,String> entry=gson.fromJson(entryString,HashMap.class);
            String timeString=entry.get("time");
            String commentString=entry.get("comment");
            String colorString=entry.get("color");
            String visitedString=entry.get("visited");
            records.add(new TempListHashMapServiceRecord(urlString, timeString,commentString,colorString,visitedString));
        }
        records.sort((a1, a2) -> a1.getTime().compareTo(a2.getTime()));

        for (TempListHashMapServiceRecord record : records) {
            HashMap<String, String> map = new HashMap<>();
            log.info("key: " + record.time + " value:" + record.url);
            map.put("url", record.url);
            map.put("time",record.getTime());
            map.put("comment",record.getComment());
            map.put("color",record.getColor());
            map.put("visited",record.getVisited());
            result.add(map);
        }
        return result;
    }


    public Boolean updateRecord(String username,String url,String time,String comment,String color,String visited){
        String key = getKey(username);
        String hashkey=url;

        Gson gson=new Gson();
        HashMap<String,String> entry=new HashMap<>();
        entry.put("comment",comment);
        entry.put("color",color);
        entry.put("visited",visited);
        String value=gson.toJson(entry);
        long expireOfKey = redisTemplate.getExpire(key);//-1永久  -2w无key
        if(expireOfKey==-2){
            return false;
        }
        else{
            redisTemplate.opsForHash().put(key, url,value);
            return true;
        }

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
@AllArgsConstructor
class TempListHashMapServiceRecord {
    String url;
    String time;
    String comment;
    String color;
    String visited;

    public TempListHashMapServiceRecord(String url,String time ) {
        this.time = time;
        this.url = url;
        this.comment = "";
    }

    public TempListHashMapServiceRecord(String url,String time,String comment ) {
        this.time = time;
        this.url = url;
        this.comment = comment;
    }
}