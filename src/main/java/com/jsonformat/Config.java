package com.jsonformat;


/**
 * Created by zzz40500 on 15/5/31.
 * 默认配置项
 */
public class Config {

    // 变量是否是private的，如果是那么就自动产生set+get方法
    private boolean fieldPrivateMode = true;

    // 是否添加SerializedName的注解
    private boolean useSerializedName = true;

    // 生成子类时，子类的后缀名
    private String suffixStr = "";

    // 复用entity
    private boolean resuseEntity = false;

    private Config() {

    }

    private static Config config;

    public static Config getInstant() {
        if (config == null) {
            config = new Config();
            
            /*config.setFieldPrivateMode(true);
            config.setUseSerializedName(true);
            config.setSuffixStr("");
            config.setResuseEntity(false);

            config.setObjectFromDataStr();
             config.setObjectFromDataStr1("Strings.objectFromObject1");
            config.setArrayFromDataStr(, Strings.arrayFromData));
            config.setArrayFromData1Str(, Strings.arrayFromData1));*/
        }
        return config;
    }


    public boolean isFieldPrivateMode() {
        return fieldPrivateMode;
    }


    public boolean isUseSerializedName() {
        return useSerializedName;
    }

    public String getSuffixStr() {
        return suffixStr;
    }

    public static Config getConfig() {
        return config;
    }

    public static void setConfig(Config config) {
        Config.config = config;
    }



}
