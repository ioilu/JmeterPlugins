//package com.yzw.jmetersampler.testcases;
//
//import com.yzw.jmetersampler.config.DBConfig;
//import com.yzw.jmetersampler.config.RedisConfig;
//import com.yzw.jmetersampler.service.MySqlService;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.stereotype.Component;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.sql.SQLException;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest
//public class Testt {
//    @Autowired
//    RedisConfig redisConfig;
//
//    @Test
//    public void test() throws SQLException {
//        DBConfig dbConfig = new DBConfig();
//        dbConfig.setDbHost("jdbc:mysql://172.16.0.130:8301/yz_mp_ums");
//        dbConfig.setDbPassword("YZ.zk.owner@2019");
//        dbConfig.setDbUsername("app_owner");
//        MySqlService mySqlService = new MySqlService(dbConfig);
//        System.out.println(        mySqlService.executeSql("select count(*) from user"));
//
//    }
//}
