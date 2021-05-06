package com.example.demo.controller;

import cn.hutool.core.date.DateUtil;
import com.example.demo.service.FliterService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;


//用户临时的列表 当天清除
@RestController
public class TempListController {
    Logger log= LoggerFactory.getLogger("TempListController");

    @Autowired
    FliterService fliterService;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    //add
    @PostMapping("/addtemplist")
    public HashMap<String, String> add(String url, String time) {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        String username = authentication.getName();
        String key = username + "::templist";

        HashMap<String, String> result = new HashMap<>();

        int setTimeOut = 0;

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat dayformat = new SimpleDateFormat("yyyy-MM-dd");
        Date inputtime=new Date();
        String inputtimestr="";
        try {
            inputtime= format.parse(time);
            inputtimestr = format.format(inputtime);
            System.out.println("inputtime:" + inputtimestr);

        } catch (ParseException e) {
            e.printStackTrace();
        }


        long expireOfKey = redisTemplate.getExpire(key);//-1永久  -2w无key
        System.out.println("expireOfKey:" + expireOfKey);

//        if(redisTemplate.get(username + "::templist")==null){
//            setTimeOut=1;
//        }

        url = fliterService.doFliterUrl(url);

        log.info("转化后的url:" + url);
        Boolean success = redisTemplate.opsForHash().putIfAbsent(key, url,
                inputtimestr);
        //首次
        if (success) {
            if (-2 == expireOfKey) {

                try {
                    Date today = dayformat.parse(time.split(" ")[0]);
                    System.out.println("today:" + today.toString());

                    Calendar c = Calendar.getInstance();
                    c.setTime(today);
                    c.add(Calendar.DAY_OF_MONTH, 1);//利用Calendar 实现 Date日期+1天

                    Date tommorrow = c.getTime();
                    System.out.println("tommorrow:" + tommorrow.toString());
                    long temp = tommorrow.getTime() - inputtime.getTime();    //相差毫秒数
                    redisTemplate.expire(key,temp, TimeUnit.MILLISECONDS);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
            result.put("code", "0");
            result.put("message", "临时列表添加成功");
            result.put("address", url);
            log.info("addtolist success" + username + "->" + url);
        } else {
            result.put("code", "1");
            result.put("message", "临时列表中已存在记录");
            result.put("address", url);
            log.info("addtolist !success" + username + "->" + url);
        }

        return result;
    }

    @PostMapping("/addtemplist2")
    public HashMap<String, String> addtemplist2(String url) {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        String username = authentication.getName();

        HashMap<String, String> result = new HashMap<>();

        url = fliterService.doFliterUrl(url);

        log.info("转化后的url:" + url);
        Boolean success = redisTemplate.opsForHash().putIfAbsent(username + "::templist", url, DateUtil.now().toString());
        //首次
        if (success) {
            result.put("code", "0");
            result.put("message", "临时列表添加成功");
            result.put("address", url);
            log.info("addtolist success" + username + "->" + url);
        } else {
            result.put("code", "1");
            result.put("message", "临时列表中已存在记录");
            result.put("address", url);
            log.info("addtolist !success" + username + "->" + url);
        }

        return result;
    }

    @GetMapping("/gettemplist")
    public ArrayList<HashMap<String, String>> getTempList() {
        class TempListRecord {
            String url;
            String time;

            public TempListRecord(String time, String url) {
                this.time = time;
                this.url = url;
            }
        }
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        String username = authentication.getName();

        ArrayList<HashMap<String, String>> result = new ArrayList<>();
        Set<Object> keys = redisTemplate.opsForHash().keys(username + "::templist");
        ArrayList<TempListRecord> records = new ArrayList<>();
        for (Object key : keys) {
            String keystr = String.valueOf(key);
            String timestr = String.valueOf(redisTemplate.opsForHash().get(username + "::templist", key));
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

    @GetMapping("/gettemplist2")
    public ArrayList<HashMap<String, String>> getTempList2() {
        class TempListRecord {
            String url;
            String time;

            public TempListRecord(String time, String url) {
                this.time = time;
                this.url = url;
            }
        }
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        String username = authentication.getName();

        ArrayList<HashMap<String, String>> result = new ArrayList<>();
        Set<Object> keys = redisTemplate.opsForHash().keys(username + "::templist");
        ArrayList<TempListRecord> records = new ArrayList<>();
        for (Object key : keys) {
            String keystr = String.valueOf(key);
            String timestr = String.valueOf(redisTemplate.opsForHash().get(username + "::templist", key));
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
}
