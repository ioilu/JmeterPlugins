package com.yzw.jmetersampler.service;


import redis.clients.jedis.Jedis;

public class RedisService {
    private Jedis jedis;

    public RedisService(){}

    public RedisService(String hostUrl, int port){
        jedis = new Jedis(hostUrl, port);
    }

    public void selectDB(int dbNo){
        jedis.select(dbNo);
    }

    public String queryKey(String key){
        return jedis.get(key);
    }

    public boolean checkExists(String key){
        return jedis.exists(key);
    }

    /**
     * 增加（或覆盖）数据项
     * @param key
     * @param value
     * @return
     */
    public String setKeyValue(String key,String value){
        return jedis.set(key,value);
    }

    public void deleteKey(String key){
        jedis.del(key);
    }

    /**
     * 设置键为key的过期时间为i秒
     * @param key
     * @param i
     */
    public void setKeyExpireTime(String key,int i){
        jedis.expire(key,i);
    }

    /**
     * 获取建委key数据项的剩余时间（秒）
     * @param key
     * @return
     */
    public long getExpireSeconds(String key){
        return jedis.ttl(key);
    }


    public static void main(String[] args) {
        RedisService redisService = new RedisService("172.16.0.239",6303);
        redisService.selectDB(1);
        String s = redisService.queryKey("jicai_sso_login:11002");

        System.out.println(s);
        System.out.println(redisService.checkExists("jicai_sso_login:11002"));
        System.out.println(redisService.setKeyValue("jicai_test:ioilu","ioilu"));
        System.out.println(redisService.queryKey("jicai_test:ioilu"));
        redisService.setKeyExpireTime("jicai_test:ioilu",10);

        System.out.println(redisService.getExpireSeconds("jicai_test:ioilu"));
        redisService.deleteKey("jicai_test:ioilu");
        System.out.println(redisService.queryKey("jicai_test:ioilu"));
    }
}
