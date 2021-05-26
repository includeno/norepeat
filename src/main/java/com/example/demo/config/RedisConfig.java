package com.example.demo.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.net.UnknownHostException;
import java.time.Duration;

@Configuration
//Redis 缓存配置类
public class RedisConfig extends CachingConfigurerSupport {

    @Value("${spring.redis.database}")
    private int database;
    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private int port;
    @Value("${spring.redis.password}")
    private String password;
    @Value("${spring.redis.Lettuce.pool.max-active}")
    private int maxActive;
    @Value("${spring.redis.Lettuce.pool.max-wait}")
    private int maxWait;
    @Value("${spring.redis.Lettuce.pool.max-idle}")
    private int maxIdle;
    @Value("${spring.redis.Lettuce.pool.min-idle}")
    private int minIdle;
    @Value("${spring.redis.timeout}")
    private int timeout;
    @Value("${dockerconfig}")
    private String dockerconfig;
    

    @Autowired
    private LettuceConnectionFactory lettuceConnectionFactory;

    @Bean
    public LettuceConnectionFactory redisConnectionFactory() throws UnknownHostException {

        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration ();
        
        
        String osName = System.getProperty("os.name");
        if (osName.startsWith("Mac OS")) {
            // 苹果 非docker环境
            System.out.println("Mac OS");
        } else if (osName.startsWith("Windows")) {
            // windows 非docker环境

            System.out.println("Windows");
        } else {
            //docker内部使用Linux 外部环境是mac
            if(dockerconfig.equals("mac")){
                // unix or linux
                System.out.println("Linux");
                host="host.docker.internal";
            }
            //docker内部使用Linux 外部环境是linux 服务器
            else if(dockerconfig.equals("linux")){
                
            }
        }
        redisStandaloneConfiguration.setHostName(host);
        redisStandaloneConfiguration.setPort(port);
        redisStandaloneConfiguration.setDatabase(database);
        redisStandaloneConfiguration.setPassword(RedisPassword.of(password));

        LettuceClientConfiguration.LettuceClientConfigurationBuilder lettuceClientConfigurationBuilder=LettuceClientConfiguration.builder();
        lettuceClientConfigurationBuilder.commandTimeout(Duration.ofMillis(timeout));
        LettuceConnectionFactory factory = new LettuceConnectionFactory(redisStandaloneConfiguration,
                lettuceClientConfigurationBuilder.build());
//        factory.afterPropertiesSet();
        System.out.println("LettuceConnectionFactory");
        return factory;
    }

    
    @Bean(name = "redisTemplate")
    public RedisTemplate<String, String> redisTemplate2(LettuceConnectionFactory lettuceConnectionFactory) throws UnknownHostException {
        System.out.println("redisTemplate2");
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(lettuceConnectionFactory);

        // 设置value的序列化规则和 key的序列化规则
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());

        // 设置Hash的序列化规则和 key的序列化规则
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }
//    @Bean
//    public RedisCacheManager redisCacheManager() {
//        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(lettuceConnectionFactory);
//
//        RedisSerializer stringSerializer = new StringRedisSerializer();
//        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
//        ObjectMapper om = new ObjectMapper();
//        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.PUBLIC_ONLY);
//        //om.activateDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
//        om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,ObjectMapper.DefaultTyping.NON_FINAL);
//        jackson2JsonRedisSerializer.setObjectMapper(om);
//
//        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
//                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer));
//        redisCacheConfiguration.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer));
//        redisCacheConfiguration.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer));
//
//        return new RedisCacheManager(redisCacheWriter, redisCacheConfiguration);
//    }


//    @Bean
//    public RedisCacheManager redisCacheManager() {
//        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(lettuceConnectionFactory);
//
//        RedisSerializer stringSerializer = new StringRedisSerializer();
//        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
//        ObjectMapper om = new ObjectMapper();
//        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.PUBLIC_ONLY);
//        //om.activateDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
//        om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,ObjectMapper.DefaultTyping.NON_FINAL);
//        jackson2JsonRedisSerializer.setObjectMapper(om);
//
//        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
//                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer));
//        redisCacheConfiguration.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()));
//        redisCacheConfiguration.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jackson2JsonRedisSerializer));
//
//        return new RedisCacheManager(redisCacheWriter, redisCacheConfiguration);
//    }
}