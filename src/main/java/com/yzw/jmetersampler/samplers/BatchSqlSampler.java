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

public class BatchSqlSampler extends AbstractJavaSamplerClient {
    private DBConfig dbConfig = new DBConfig();
    private JSONArray jsonArrayResult = new JSONArray();
    private int intResult;
    private boolean boolResult;



    @Override
    public SampleResult runTest(JavaSamplerContext javaSamplerContext) {
        SampleResult result = new SampleResult();   //实例化来自ApacheJmeter_core.jar包中的SampleResult类
        result.sampleStart();  //测试开始的时间戳

        dbConfig.DbHost = javaSamplerContext.getParameter("DbHost"); //获取Jmeter页面上DbHost的方法
        dbConfig.DbUsername = javaSamplerContext.getParameter("DbUsername");  //获取Jmeter页面上DbPassword的方法
        dbConfig.DbPassword = javaSamplerContext.getParameter("DbPassword");  //获取Jmeter页面上DbUsername的方法
        String sqlsString = "";
        try {
            for (int i = 0; i<5; i++)
            {
                result.setResponseData(result.getResponseDataAsString().concat("\nsql" + (i+1) + "执行结果：\n"), "UTF-8");

                MySqlService mySqlService = new MySqlService(dbConfig);
                String sql = javaSamplerContext.getParameter("sql" + (i+1));
                if(sql.isEmpty()){
                    result.setResponseData(result.getResponseDataAsString().concat("sql为空"  ), "UTF-8");
                    result.setResponseData(result.getResponseDataAsString().concat("\n==========================="));
                    continue;
                }
                sql = sql.trim().toLowerCase();
                sqlsString += "\n" + sql;
                String[] s = sql.trim().split(" ");
                String sqlType = s[0];
                if ("select".equals(sqlType)) {
                    jsonArrayResult = mySqlService.query(sql);
                    String jsonString = "[ \n";
                    for (Object object : jsonArrayResult) {
                        jsonString += StringUtil.jsonStringFormater(StringUtil.fortmatObjectToJsonString(object));
                        jsonString += ",";
                    }
                    jsonString = jsonString.substring(0, jsonString.length() - 1);
                    jsonString += "\n]";
                    result.setResponseData(result.getResponseDataAsString().concat( jsonString ), "UTF-8");
                } else if ("update".equals(sqlType)) {
                    intResult = mySqlService.update(sql);
                    if (intResult >= 1) {
                        result.setResponseData(result.getResponseDataAsString().concat("更新数据条数：" + intResult ),"UTF-8");
                    }
                } else if ("delete".equals(sqlType)) {
                    boolResult = mySqlService.delete(sql);
                    if(boolResult == true){
                        result.setResponseData(result.getResponseDataAsString().concat("删除成功。"),"UTF-8");
                    }
                } else if ("insert".equals(sqlType)) {
                    boolResult = mySqlService.insert(sql);
                    if(boolResult == true){
                        result.setResponseData(result.getResponseDataAsString().concat("插入成功。"),"UTF-8");
                    }
                } else {
                    result.setResponseData(result.getResponseDataAsString().concat("sql错误，请重新检查入参。"  ),"UTF-8");
                }
                result.setResponseData(result.getResponseDataAsString().concat("\n==========================="));

            }
            result.setSamplerData(sqlsString);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        result.sampleEnd();   //测试结束的时间戳
        result.setSuccessful(true);  //设置成功的
        result.setResponseCodeOK();   //设置响应code码
        result.setDataEncoding("UTF-8");   //设置数据编码格式
        return result;
    }

    @Override
    public Arguments getDefaultParameters() {
        Arguments argument = new Arguments();
        argument.addArgument("DbHost", "jdbc:mysql://172.0.0.1:8080/test");
        argument.addArgument("DbUsername","admin");
        argument.addArgument("DbPassword","admin123");
        argument.addArgument("sql1","");
        argument.addArgument("sql2","");
        argument.addArgument("sql3","");
        argument.addArgument("sql4","");
        argument.addArgument("sql5","");
        return argument;
    }

    public static void main(String[] args) {
        BatchSqlSampler batchSqlSampler = new BatchSqlSampler();
        JavaSamplerContext arg0 = new JavaSamplerContext(batchSqlSampler.getDefaultParameters());
        batchSqlSampler.setupTest(arg0);
//        SampleResult sampleResult = ;
//        System.out.println("================");
        System.out.println(        batchSqlSampler.runTest(arg0).getResponseDataAsString());
//        System.out.println("================");

        batchSqlSampler.teardownTest(arg0);
    }
}
