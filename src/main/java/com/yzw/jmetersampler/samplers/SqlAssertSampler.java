package com.yzw.jmetersampler.samplers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yzw.jmetersampler.config.DBConfig;
import com.yzw.jmetersampler.service.MySqlService;
import com.yzw.jmetersampler.utils.StringUtil;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqlAssertSampler extends AbstractJavaSamplerClient {

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
        String assertString = javaSamplerContext.getParameter("assertString");
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
        List<String> assertResultList = new ArrayList<String>();
        if (assertString.length() > 0){
            String[] kvArray = assertString.trim().split("\\|\\|");
            jsonString += "\n\n\n\n\n\n\n=========================================\n";
            JSONObject jsonObject = jsonArrayResult.getJSONObject(0);
            Map<String, String> kvMap = new HashMap<String, String>();
            for (String kvString : kvArray){
                String[] temp = kvString.split(":");
                kvMap.put(temp[0], temp[1]);
                jsonString += "预期结果：" + temp[0].trim() + " = " + temp[1].trim() + ",实际结果：" + jsonObject.getString(temp[0].trim()).trim() + "\n";
                if (!jsonObject.getString(temp[0].trim()).trim().equals(temp[1].trim())){
                    result.setSuccessful(false);
                }
            }
        }
        result.setResponseData(jsonString,"UTF-8");  //设置响应数据的格式
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
        argument.addArgument("DbPassword","admin");
        argument.addArgument("sql","select * from order limit 1");
//        格式key，value之前用":",kv键值对之间使用"||"
        argument.addArgument("assertString","");
        return argument;
    }

//    public List<String> assertFunc(String assertString, JSONArray jsonArray){
//
//        return result;
//    }


    public static void main(String[] args) {
        SqlAssertSampler sqlAssertSampler = new SqlAssertSampler();
        JavaSamplerContext arg0 = new JavaSamplerContext(sqlAssertSampler.getDefaultParameters());
        sqlAssertSampler.setupTest(arg0);
//        SampleResult sampleResult = ;
        System.out.println("================");
        sqlAssertSampler.runTest(arg0);
        System.out.println(sqlAssertSampler.runTest(arg0).getResponseDataAsString());
        System.out.println("================");

        sqlAssertSampler.teardownTest(arg0);

    }
}
