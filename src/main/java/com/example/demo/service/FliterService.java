package com.example.demo.service;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class FliterService {
    Logger log= LoggerFactory.getLogger("FliterService");
    @Value("fliternames")
    String fliternames;

    public String doFliterUrl(String url) {
        log.info("过滤url:" + url);
        if (url.startsWith("https://www.baidu.com/link?url=") || url.startsWith("http://www.baidu.com/link?url=")) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            try {
                Response response = client.newCall(request).execute();
                String result=response.request().url().toString();
                System.out.println(result);
                response.close();
                return (result);
            } catch (IOException e) {
                log.warn("过滤url: IOException " + url);
                return url;
            }
        }
        final String finalurl = url;
        List<String> array = Arrays.asList(fliternames.split(","));
        array = array.stream().parallel().filter(x -> finalurl.contains(x)).collect(Collectors.toList());
        if (array.size() > 0) {
            url = url.split("/?")[0];
            return url;
        } else {
            return url;
        }


    }
}
