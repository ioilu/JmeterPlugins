package com.yzw.jmetersampler.service;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Set;

/**
 * @author lgh09946
 * @date 2015/2/11
 */
public class RedisClient {
    private volatile static RedisClient instance = null;
    private final int DEFAULT_TIMEOUT = 100000;
    /**
     * 非切片连接池
     */
    private JedisPool jedisPool;
    private int defaultExpireTime;

    private static final Object locker = new Object();

    private RedisClient() {
        initialPool();
    }

    public static RedisClient getInstance() {
        //先检查实例是否存在，如果不存在才进入下面的同步块
        if (instance == null) {
            //同步块，线程安全的创建实例
            synchronized (locker) {
                //再次检查实例是否存在，如果不存在才真正的创建实例
                if (instance == null) {
                    instance = new RedisClient();
                }
            }
        }
        return instance;
    }

    public Jedis getResource() {
        return jedisPool.getResource();
    }

    /**
     * 初始化非切片池
     */
    private synchronized void initialPool() {
        // 池基本配置
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(100);
        config.setMaxIdle(5);
        defaultExpireTime = 50;
        jedisPool = new JedisPool(config, "172.16.0.239",
                26302, DEFAULT_TIMEOUT);
    }

    public String getKey(String key) {
        Jedis jedis = this.getResource();
        try {
            return jedis.get(key);
        } catch (Exception e) {
            System.out.println(e.getMessage());
//            throw e;
        } finally {
            jedisPool.destroy();
        }
        return key;
    }

    public void setKey(String key, String value) throws Exception {
        setKey(key, value, defaultExpireTime);
    }

    public void setKey(String key, String value, int seconds) {
        Jedis jedis = this.getResource();
        try {
            jedis.set(key, value);
            if (seconds != -1) {
                jedis.expire(key, seconds);
            }
        } catch (Exception e) {
//            throw e;
            System.out.println(e.getMessage());

        } finally {
            //jedisPool.returnResource(jedis);
        }
    }

    public Long delKey(String[] key) throws Exception {
        Jedis jedis = this.getResource();
        try {
            return jedis.del(key);
        } catch (Exception e) {
            throw e;
        } finally {
            //jedisPool.returnResource(jedis);
        }
    }

    public void setExpireTime(String key, int seconds) {
        Jedis jedis = this.getResource();
        try {
            jedis.expire(key, seconds);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //jedisPool.returnResource(jedis);
        }
    }

    public Long delKey(String key){
        Jedis jedis = this.getResource();
        try {
            return jedis.del(key);
        } catch (Exception e) {
            e.printStackTrace();
//            throw e;
        } finally {
            //jedisPool.returnResource(jedis);
        }
        return null;
    }

    public Set<String> selectKeys(String pattern) {
        Jedis jedis = this.getResource();
        try {
            return jedis.keys(pattern);
        } catch (Exception e) {
            System.out.println(e.getMessage());

//            throw e;
        } finally {
            //jedisPool.returnResource(jedis);
        }
        return null;

    }

    public Long sadd(String set, String... member) {
        Jedis jedis = this.getResource();
        try {
            return jedis.sadd(set, member);
        } catch (Exception e) {
            System.out.println(e.getMessage());

//            throw e;
        } finally {
            //jedisPool.returnResource(jedis);
        }
        return null;
    }

    public String spop(String set) {
        Jedis jedis = this.getResource();
        try {
            return jedis.spop(set);
        } catch (Exception e) {
            System.out.println(e.getMessage());

//            throw e;
        } finally {
           // jedisPool.returnResource(jedis);
        }
        return null;

    }

    public Set<String> smembers(String set) {
        Jedis jedis = this.getResource();
        try {
            return jedis.smembers(set);
        } catch (Exception e) {
            System.out.println(e.getMessage());

//            throw e;
        } finally {
            //jedisPool.returnResource(jedis);
        }
        return null;

    }

    public Long srem(String set, String... member) {
        Jedis jedis = this.getResource();
        try {
            return jedis.srem(set, member);
        } finally {
            //jedisPool.returnResource(jedis);
        }
    }

    public void hset(String key, String field, String value, int seconds) {
        Jedis jedis = this.getResource();
        try {
            jedis.hset(key, field, value);
            if (seconds != -1) {
                jedis.expire(key, seconds);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());

//            throw e;
        } finally {
            //jedisPool.returnResource(jedis);
        }
    }

    public String hget(String key, String field) {
        Jedis jedis = this.getResource();
        try {
            return jedis.hget(key, field);
        } catch (Exception e) {
            System.out.println(e.getMessage());

//            throw e;
        } finally {
            //jedisPool.returnResource(jedis);
        }
        return null;

    }

    public static void main(String[] args) {
        RedisClient.getInstance().getKey("jicai_sso_login:11002");
    }
}
