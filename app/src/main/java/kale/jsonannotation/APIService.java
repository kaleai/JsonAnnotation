package kale.jsonannotation;


import kale.net.json.annotation.Json2Model;

/**
 * @author Jack Tony
 * @date 2015/8/16
 */
public interface APIService {

    // 简单格式
    @Json2Model(modelName = "Simple", jsonStr = "{\n"
            + "    \"id\": 100,\n"
            + "    \"body\": \"It is my post\",\n"
            + "    \"number\": 0.13,\n"
            + "    \"created_at\": \"2014-05-22 19:12:38\"\n"
            + "}")
    String TEST_SIMPLE = "test/simple"; // api url


    // 数组格式
    @Json2Model(modelName = "Array", jsonStr = "[{\n"
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
            + "}]")
    String TEST_ARRAY = "test/array"; // api url

    // 嵌套类型
    @Json2Model(modelName = "Nest", jsonStr = "{\n"
            + "    \"id\": 100,\n"
            + "    \"body\": \"It is my post\",\n"
            + "    \"number\": 0.13,\n"
            + "    \"created_at\": \"2014-05-22 19:12:38\",\n"
            + "    \"child\": {\n"
            + "        \"id\": 200,\n"
            + "        \"name\": \"haha\"\n"
            + "    }\n"
            + "}")
    String TEST_NEST = "test/nest";


}
