package kale.jsonannotation;


import kale.net.json.annotation.Json2Model;

/**
 * @author Jack Tony
 * @date 2015/8/16
 * 这里我为了方便测试把jsonStr独立出来了
 */
public interface APIService {

    String simpleStr = "{\n"
            + "    \"id\": 100,\n"
            + "    \"body\": \"It is my post\",\n"
            + "    \"number\": 0.13,\n"
            + "    \"created_at\": \"2014-05-22 19:12:38\"\n"
            + "}";
    // 简单格式
    @Json2Model(modelName = "Simple", jsonStr = simpleStr)
    String TEST_SIMPLE = "test/simple"; // api url


    String arrayStr = "[{\n"
            + "    \"id\": 100,\n"
            + "    \"body\": \"It is my post1\",\n"
            + "    \"number\": 0.13,\n"
            + "    \"created_at\": \"2014-05-20 19:12:38\"\n"
            + "},\n"
            + "{\n"
            + "    \"id\": 101,\n"
            + "    \"body\": \"It is my post2\",\n"
            + "    \"number\": 0.14,\n"
            + "    \"created_at\": \"2014-05-22 19:12:38\"\n"
            + "}]";
    // 数组格式
    @Json2Model(modelName = "Array", jsonStr = arrayStr)
    String TEST_ARRAY = "test/array"; // api url

    String nestStr = "{\n"
            + "    \"id\": 100,\n"
            + "    \"body\": \"It is my post\",\n"
            + "    \"number\": 0.13,\n"
            + "    \"created_at\": \"2014-05-22 19:12:38\",\n"
            + "    \"child\": {\n"
            + "        \"id\": 200,\n"
            + "        \"name\": \"haha\"\n"
            + "    }\n"
            + "}";
    // 嵌套类型
    @Json2Model(modelName = "Nest", jsonStr = nestStr)
    String TEST_NEST = "test/nest";


}
