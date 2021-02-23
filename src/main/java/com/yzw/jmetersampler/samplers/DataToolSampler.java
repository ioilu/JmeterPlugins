package com.yzw.jmetersampler.samplers;

import com.alibaba.fastjson.JSONObject;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.AbstractJavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;


public class DataToolSampler extends AbstractJavaSamplerClient {
    @Override
    public SampleResult runTest(JavaSamplerContext javaSamplerContext) {
        SampleResult result = new SampleResult();   //实例化来自ApacheJmeter_core.jar包中的SampleResult类
        result.sampleStart();  //测试开始的时间戳

        String funcName = javaSamplerContext.getParameter("FuncName");
        String params = javaSamplerContext.getParameter("params");
        switch (funcName) {
            case "ParamsToJSONString":
                JSONObject jsonObject = new JSONObject();
                String[] paramString = params.trim().split("\\|");
                for (String s : paramString) {
                    String[] kv = s.split(":");
                    String value = kv[1].trim();
                    if (value.startsWith("number")){
                        value = value.replace("number","");
                        if (value.contains(".")){
                            System.out.println(Float.valueOf(value).getClass());
                            jsonObject.put(kv[0].trim(),Float.valueOf(value));
                        }else {
                            jsonObject.put(kv[0].trim(),Integer.valueOf(value));
                        }
                    }
                    jsonObject.put(kv[0].trim(),value);
                }
                result.setResponseData(jsonObject.toJSONString(),"UTF-8");
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
        argument.addArgument("FuncName","ParamsToJSONString");
        argument.addArgument("params", "key1:value1|key2:value2");
        return argument;
    }

    public static void main(String[] args) {
        DataToolSampler dataTypeToolSampler = new DataToolSampler();
        Arguments arguments = new Arguments();
        arguments.addArgument("FuncName","ParamsToJSONString");
        arguments.addArgument("params", "key1:value1|key2:value2|key3:123|key4:1.1");



        JavaSamplerContext arg0 = new JavaSamplerContext(arguments);
        dataTypeToolSampler.runTest(arg0);
    }
}
