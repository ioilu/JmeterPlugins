package com.yzw.jmetersampler.samplers;

import com.alibaba.fastjson.JSONArray;
import com.yzw.jmetersampler.config.RedisConfig;
import com.yzw.jmetersampler.config.RedisMethodsEnum;
import com.yzw.jmetersampler.service.RedisService;
import com.yzw.jmetersampler.utils.StringUtil;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerClient;
import org.apache.jmeter.protocol.java.sampler.JavaSamplerContext;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.logging.log4j.core.util.Integers;

public class RedisSampler implements JavaSamplerClient {


    @Override
    public void setupTest(JavaSamplerContext javaSamplerContext) {
        System.out.println("测试开始");
    }

    @Override
    public SampleResult runTest(JavaSamplerContext javaSamplerContext) {
        SampleResult result = new SampleResult();   //实例化来自ApacheJmeter_core.jar包中的SampleResult类
        RedisConfig redisConfig = new RedisConfig();
        redisConfig.RedisHost = javaSamplerContext.getParameter("RedisUrl");  //获取Jmeter页面上RedisUrl的方法
        redisConfig.RedisPort = Integer.valueOf(javaSamplerContext.getParameter("Port"));  //获取Jmeter页面上Port的方法
        redisConfig.RedisDatabaseIndex = Integer.valueOf(javaSamplerContext.getParameter("DBIndex"));
        String Method = javaSamplerContext.getParameter("Method");

        String Parameter = javaSamplerContext.getParameter("Parameter");
        int FormatData = Integer.valueOf(javaSamplerContext.getParameter("FormatData"));

        result.sampleStart();  //测试开始的时间戳
        RedisService redisService = new RedisService(redisConfig.RedisHost,redisConfig.RedisPort);

//        如果db为大于0的整数则设置db，否则默认使用db0
        if (redisConfig.RedisDatabaseIndex >= 0){
            redisService.selectDB(redisConfig.RedisDatabaseIndex);
        }else {
            redisService.selectDB(0);
        }

        //根据不同的MethodId入参，执行redis请求
        //exists判断是否存在，get查询，set设置，ttl获取过期时间，del删除，expire设置过期时间（秒）
        String reusltData = "";
        if (Method.equals(RedisMethodsEnum.EXISTS.getFunction()) ){
            reusltData = String.valueOf(redisService.checkExists(Parameter));
        }else if (Method.equals(RedisMethodsEnum.GET.getFunction())){
            reusltData = redisService.queryKey(Parameter);
        }else if (Method.equals(RedisMethodsEnum.SETT.getFunction())){
            String[] a = Parameter.split("|");
            reusltData = redisService.setKeyValue(a[0],a[1]);
        }else if (Method.equals(RedisMethodsEnum.TTL.getFunction())){
            reusltData = String.valueOf(redisService.getExpireSeconds(Parameter));
        }else if (Method.equals(RedisMethodsEnum.DEL.getFunction())){
            redisService.deleteKey(Parameter);
        }else if (Method.equals(RedisMethodsEnum.EXPIRE.getFunction())){
            String[] a = Parameter.split("|");
            int seconds = Integers.parseInt(a[1]);
            redisService.setKeyExpireTime(a[0], seconds);
        }else {
            reusltData = "错误的Method";
        }

        //格式化字符串
        reusltData = formatResultData(FormatData,reusltData);

        //设置jmeter响应数据
        result.setResponseData(reusltData,"UTF-8");


//        result.setResponseData(interfaceName+":"+methodName,"UTF-8");  //设置响应数据的格式
        result.sampleEnd();   //测试结束的时间戳
        result.setSuccessful(true);  //设置成功的
        result.setResponseCodeOK();   //设置响应code码
        result.setDataEncoding("UTF-8");   //设置数据编码格式
        return result;
    }

    @Override
    public void teardownTest(JavaSamplerContext javaSamplerContext) {
        System.out.print("测试结束");
    }

    @Override
    public Arguments getDefaultParameters() {
        Arguments argument = new Arguments();
        argument.addArgument("RedisUrl","172.16.0.239");
        argument.addArgument("Port","6303");
        argument.addArgument("DBIndex","输入db序号(大于等于0)，不输入默认为db0");
        argument.addArgument("Method","exists判断是否存在，get查询，set设置，ttl获取过期时间，del删除，expire设置过期时间（秒）");
        argument.addArgument("Parameter","请输入参数，多参数多时候以\"|\"分隔");
        argument.addArgument("FormatData","为1表示以json格式输出，为2表示以JSONArray格式化输出,为0表示以字符串格式输出");

        return argument;
    }

    //判断是否需要格式化输出,为1表示以json格式输出，为2表示以JSONArray格式化输出,为0表示以字符串输出
    public String formatResultData(int formatData,String resultData){
        String formatedData = "";
        if(formatData == 1) {
            formatedData = StringUtil.jsonStringFormater(resultData);
        } else if (formatData == 2) {
            formatedData = StringUtil.jsonArrayToStringAndFormater(JSONArray.parseArray(resultData));
        } else if (formatData == 0) {
            //不做处理
        } else {
            formatedData = "FormatData参数错误";
        }
        return formatedData;
    }
    public static void main(String[] args) {
        RedisSampler rs = new RedisSampler();
        Arguments arguments = new Arguments();
        arguments.addArgument("RedisUrl","172.16.0.239");
        arguments.addArgument("Port","6303");
        arguments.addArgument("DBIndex","1");
        arguments.addArgument("Method","get");
        arguments.addArgument("Parameter","jicai_sso_login:11002");
        arguments.addArgument("FormatData","1");

        JavaSamplerContext arg0 = new JavaSamplerContext(arguments);

        System.out.println(rs.runTest(arg0).getResponseDataAsString());


    }
}
