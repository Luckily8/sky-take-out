package com.sky.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Redis配置类
 */
@Configuration
@Slf4j
public class RedisConfiguration {

    /**
     * 1.导入maven依赖
     * 2.配置Redis连接信息
     * 3.配置RedisTemplate
     * 4.通过@Bean注入RedisTemplate
     */
    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        log.info("开始配置RedisTemplate...");
        RedisTemplate redisTemplate = new RedisTemplate();
        //设置连接工厂
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        //设置序列化器 字符串
        redisTemplate.setKeySerializer(redisTemplate.getStringSerializer());
        return redisTemplate;
    }
}
