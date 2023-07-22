package org.example.configs;

import org.example.models.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class CacheConfig {

    @Value("${redis-host-url}")
    String host;

    @Value("${redis-host-port}")
    int port;

    @Value("${redis-auth-password}")
    String password;

    // This bean will help us in establishing connection to redis server
    @Bean
    public LettuceConnectionFactory getConnection(){
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(host,port);
        // since password is not taken in any constructor
        configuration.setPassword(password);

        // now establishing connection
        LettuceConnectionFactory lettuceConnectionFactory = new LettuceConnectionFactory(configuration);
        return lettuceConnectionFactory;
    }

    /* For executing any command using lettuceConnectionFactory, we need to know about
    "Redis template"(similar thing in-case of kafka when we need to consume messages)

    --> Search for the class redisTemplate.class
     */

    @Bean
    public RedisTemplate<String, User> getTemplate(){
        // Having a generic template by using object class, whenever we need specific template
        // we typecast that java object like student,admin,transaction etc
        RedisTemplate<String,User> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(getConnection());
        /*
        Need to use serializer and de-serializer to turn java data types to redis data types
        and vice-versa
         */

        // from java string to redis string
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        /*
        JdkSerializationRedisSerializer has a method called serialize which takes java object
        and converts it into bytes, these bytes are stored as string on redis server

        deSerialize takes bytes and converts into java object
         */
        redisTemplate.setValueSerializer(new JdkSerializationRedisSerializer());

        return redisTemplate;
    }
}
