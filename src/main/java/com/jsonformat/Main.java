package com.jsonformat;

public class Main {

    public static void main(String[] args) {
        String jsonStr;
        
        jsonStr = "{\n" +
                "    \"name\": \"kale\",\n" +
                "    \"gender\": \"man\",\n" +
                "    \"age\": 15,\n" +
                "    \"height\": \"140cm\",\n" +
                "}";
        
        jsonStr = "{\n" +
                "    \"name\": \"jack\",\n" +
                "    \"gender\": \"man\",\n" +
                "    \"age\": 15,\n" +
                "    \"height\": \"140cm\",\n" +
                "    \"addr\": {\n" +
                "        \"province\": \"fujian\",\n" +
                "        \"city\": \"quanzhou\",\n" +
                "        \"code\": \"300000\"\n" +
                "    },\n" +
                "    \"hobby\": [\n" +
                "        {\n" +
                "            \"name\": \"billiards\",\n" +
                "            \"code\": \"1\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"name\": \"computerGame\",\n" +
                "            \"code\": \"2\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";
        
        jsonStr = "{\n"
                + "    \"id\": 100,\n"
                + "    \"body\": \"It is my post\",\n"
                + "    \"number\": 0.13,\n"
                + "    \"created_at\": \"2014-05-22 19:12:38\",\n"
                + "    \"foo2\": {\n"
                + "        \"id\": 200,\n"
                + "        \"name\": \"haha\"\n"
                + "    }\n"
                + "}";

        jsonStr = "{\n"
                + "  \"status\": 1,\n"
                + "  \"data\": \n"
                + "  {\n"
                + "    \"more\": 1,\n"
                + "    \"next_start\": 24,\n"
                + "    \"object_list\": [{\n"
                + "        \"photo\": \n"
                + "        {\n"
                + "          \"width\": 480,\n"
                + "          \"height\": 698,\n"
                + "          \"path\": \"http://cdn.duitang.com/uploads/item/201508/13/20150813235841_2FjJA.png\"\n"
                + "        },\n"
                + "        \"msg\": \"pվ\",\n"
                + "        \"id\": 427355390,\n"
                + "        \"buyable\": 0,\n"
                + "        \"source_link\": \"\",\n"
                + "        \"add_datetime\": \"23:58\",\n"
                + "        \"add_datetime_pretty\": \"1Сʱǰ\",\n"
                + "        \"add_datetime_ts\": 1439481522,\n"
                + "        \"sender_id\": 8828896,\n"
                + "        \"favorite_count\": 0,\n"
                + "        \"extra_type\": \"PICTURE\"\n"
                + "      }]\n"
                + "  }\n"
                + "}";

        JsonParserHelper jsonParser = new JsonParserHelper();

        jsonParser.parse(jsonStr, "RootClass", new JsonParserHelper.ParseListener() {
            @Override
            public void onParseComplete(String str) {
                System.out.println(str);
            }

            @Override
            public void onParseError(Exception e) {
                System.err.println(e.getMessage());
            }
        });
    }
}
