package com.example.demo.service;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FliterService {
    @Value("fliternames")
    String fliternames;

    public String doFliterUrl(String url){
        if(url.startsWith("https://www.baidu.com/link?url=")){
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();
            try {
                Response response = client.newCall(request).execute();
                System.out.println(response.request().url().toString());
                return (response.request().url().toString());
            } catch (IOException e) {
                return url;
            }
        }
        final String finalurl=url;
        List<String> array=Arrays.asList(fliternames.split(","));
        array=array.stream().parallel().filter(x->finalurl.contains(x)).collect(Collectors.toList());
        if(array.size()>0){
            url=url.split("?")[0];
            return url;
        }
        else {
            return url;
        }
    }
}
