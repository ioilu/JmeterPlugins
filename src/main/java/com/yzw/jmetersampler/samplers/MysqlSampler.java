package com.yzw.jmetersampler.samplers;

import com.alibaba.fastjson.JSONArray;
import com.yzw.jmetersampler.config.DBConfig;
import com.yzw.jmetersampler.service.MySqlService;
import com.yzw.jmetersampler.utils.StringUtil;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

import java.sql.SQLException;

public class MysqlSampler extends AbstractJavaSamplerClient {

    private DBConfig dbConfig = new DBConfig();
    private JSONArray jsonArrayResult = new JSONArray();
    private boolean boolResult;
    private int intResult;

//    @Override
//    public void setupTest(JavaSamplerContext javaSamplerContext) {
//        System.out.println("测试开始");
//    }

    public SampleResult runTest(JavaSamplerContext javaSamplerContext) {
        SampleResult result = new SampleResult();   //实例化来自ApacheJmeter_core.jar包中的SampleResult类
        result.sampleStart();  //测试开始的时间戳
        result.setSuccessful(true);  //设置成功的
        dbConfig.DbHost = javaSamplerContext.getParameter("DbHost"); //获取Jmeter页面上DbHost的方法
        dbConfig.DbUsername = javaSamplerContext.getParameter("DbUsername");  //获取Jmeter页面上DbPassword的方法
        dbConfig.DbPassword = javaSamplerContext.getParameter("DbPassword");  //获取Jmeter页面上DbUsername的方法
        String sql = javaSamplerContext.getParameter("sql");

        //解析sql判断走什么逻辑
        sql = sql.trim().toLowerCase();
        result.setSamplerData(sql);
        String[] s = sql.trim().split(" ");
        String sqlType = s[0];
        MySqlService mySqlService = null;
        String jsonString = "[ \n";
        try {
            mySqlService = new MySqlService(dbConfig);
            if ("select".equals(sqlType)) {
                jsonArrayResult = mySqlService.query(sql);
                for (Object object : jsonArrayResult) {
                    jsonString += StringUtil.jsonStringFormater(StringUtil.fortmatObjectToJsonString(object));
                    jsonString += ",";
                }
                jsonString = jsonString.substring(0, jsonString.length() - 1);
                jsonString += "\n]";
                result.setResponseData(jsonString,"UTF-8");  //设置响应数据的格式
            } else if ("update".equals(sqlType)) {
                intResult = mySqlService.update(sql);
                if (intResult >= 1) {
                    result.setResponseData("更新数据条数：" + intResult, "UTF-8");
                }
            } else if ("delete".equals(sqlType)) {
                boolResult = mySqlService.delete(sql);
                if(boolResult == true){
                    result.setResponseData("删除成功。","UTF-8");
                }
            } else if ("insert".equals(sqlType)) {
                boolResult = mySqlService.insert(sql);
                if(boolResult == true){
                    result.setResponseData("插入成功。","UTF-8");
                }
            } else {
                result.setResponseData("sql错误，请重新检查入参。","UTF-8");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        result.sampleEnd();   //测试结束的时间戳
        result.setResponseCodeOK();   //设置响应code码
        result.setDataEncoding("UTF-8");   //设置数据编码格式
        return result;
    }

//    @Override
//    public void teardownTest(JavaSamplerContext javaSamplerContext) {
//        System.out.print("测试结束");
//    }

    @Override
    public Arguments getDefaultParameters() {
        Arguments argument = new Arguments();
        argument.addArgument("DbHost", "jdbc:mysql://172.0.0.1:8080/test");
        argument.addArgument("DbUsername","admin");
        argument.addArgument("DbPassword","admin123");
        argument.addArgument("sql","select * from order_trade limit 1\n");
        return argument;
    }

//    public List<String> assertFunc(String assertString, JSONArray jsonArray){
//
//        return result;
//    }


    public static void main(String[] args) {
        MysqlSampler sqlAssertExtractSampler = new MysqlSampler();
        JavaSamplerContext arg0 = new JavaSamplerContext(sqlAssertExtractSampler.getDefaultParameters());
        sqlAssertExtractSampler.setupTest(arg0);
//        SampleResult sampleResult = ;
        System.out.println("================");
        sqlAssertExtractSampler.runTest(arg0);
        System.out.println(sqlAssertExtractSampler.runTest(arg0).getResponseDataAsString());
        System.out.println("================");

        sqlAssertExtractSampler.teardownTest(arg0);

    }
}
