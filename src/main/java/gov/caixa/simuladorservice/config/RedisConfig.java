package gov.caixa.simuladorservice.config;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@ApplicationScoped
public class RedisConfig {

    @Produces
    @ApplicationScoped
    public JedisPool jedisPool() {
        String redisHost = System.getenv().getOrDefault("REDIS_HOST", "localhost");
        int redisPort = Integer.parseInt(System.getenv().getOrDefault("REDIS_PORT", "6379"));

        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(10);

        return new JedisPool(poolConfig, redisHost, redisPort);
    }
}
