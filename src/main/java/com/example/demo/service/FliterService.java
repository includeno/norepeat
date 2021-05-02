package com.example.demo.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
        final String finalurl=url;
        List<String> array=Arrays.asList(fliternames.split(","));
        array=array.stream().parallel().filter(x->finalurl.contains(x)).collect(Collectors.toList());
        if(array.size()>0){
            url=url.split("/?")[0];
            return url;
        }
        else {
            return url;
        }
    }
}
