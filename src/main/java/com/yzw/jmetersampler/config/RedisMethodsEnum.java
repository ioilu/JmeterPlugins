package com.yzw.jmetersampler.config;

public enum RedisMethodsEnum {
    EXISTS(0,"exists"),
    GET(1,"get"),
    SETT(2,"set"),
    TTL(3,"ttl"),
    DEL(4,"del"),
    EXPIRE(5,"expire");


    final int id;
    final String function;

    RedisMethodsEnum(int i, String function){
        this.id = i;
        this.function = function;
    }

    public int getId(){
        return this.id;
    }

    public String getFunction(){
        return this.function;
    }

}
