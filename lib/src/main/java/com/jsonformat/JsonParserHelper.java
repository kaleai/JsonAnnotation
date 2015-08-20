package com.jsonformat;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class JsonParserHelper {

    private List<String> mKeyWordList;

    private List<String> mFilterFields;

    private List<InnerClassEntity> mFilterClass;

    private StringBuilder mSB = new StringBuilder();

    public JsonParserHelper() {
        mKeyWordList = new ArrayList<String>();
        mFilterFields = new ArrayList<String>();
        mFilterClass = new ArrayList<InnerClassEntity>();
    }

    public interface ParseListener {

        void onParseComplete(String str);

        void onParseError(Exception e);
    }

    public void parse( String jsonStr,  String rootClassName,  ParseListener listener) {
        mSB.append("import com.google.gson.annotations.SerializedName;\n\n");
        mSB.append("public class ").append(rootClassName).append(" {\n");
        jsonStr = jsonStr.trim();
        if (jsonStr.trim().startsWith("[")) {
            JSONArray jsonArray = new JSONArray(jsonStr);

            if (jsonArray.length() > 0 && jsonArray.get(0) instanceof JSONObject) {
                handleJsonObject(jsonArray.getJSONObject(0), listener);
            }
        } else if (jsonStr.trim().startsWith("{")) {
            JSONObject json = null;
            try {
                json = new JSONObject(jsonStr);
            } catch (Exception e) {
                String jsonTS = filterAnnotation(jsonStr);
                try {
                    json = new JSONObject(jsonTS);
                } catch (Exception e2) {
                    listener.onParseError(e2);
                }
            }

            handleJsonObject(json, listener);
        }

    }


    private void handleJsonObject(JSONObject json, ParseListener listener) {
        if (json != null) {
            try {
                parseJson(json, mSB);
            } catch (Exception e2) {
                listener.onParseError(e2);
            }
        }
        mKeyWordList = null;
        mFilterFields = null;
        mSB.append("}");

        listener.onParseComplete(mSB.toString());
    }


//    private void recursionInnerClass(InnerClassEntity innerClassEntity) {
//        PsiClass[] innerClass s = innerClassEntity.getPsiClass().getInnerClasses();
//        if (innerClass s.length == 0) {
//
//            mFilterClass.add(innerClassEntity);
//        } else {
//            for (PsiClass psiClass : innerClass s) {
//                InnerClassEntity innerClassEntity1 = new InnerClassEntity();
//                innerClassEntity1.setClassName(psiClass.getName());
//                innerClassEntity1.setFields(initFilterField(psiClass));
//                innerClassEntity1.setPsiClass(psiClass);
//                innerClassEntity1.setPackName(innerClassEntity.getPackName() + innerClassEntity.getClassName() + ".");
//                recursionInnerClass(innerClassEntity1);
//            }
//        }
//    }


    public String filterAnnotation(String str) {

        String temp = str.replaceAll("/\\*" +
                "[\\S\\s]*?" +
                "\\*/", "");
        return temp.replaceAll("//[\\S\\s]*?\n", "");

    }


    public void parseJson(JSONObject json, StringBuilder stringBuilder) {


        //50 java keywords
        mKeyWordList.add("abstract");
        mKeyWordList.add("assert");
        mKeyWordList.add("boolean");
        mKeyWordList.add("break");
        mKeyWordList.add("byte");
        mKeyWordList.add("case");
        mKeyWordList.add("catch");
        mKeyWordList.add("char");
        mKeyWordList.add("class");
        mKeyWordList.add("const");
        mKeyWordList.add("continue");
        mKeyWordList.add("default");
        mKeyWordList.add("do");
        mKeyWordList.add("double");
        mKeyWordList.add("else");
        mKeyWordList.add("enum");
        mKeyWordList.add("extends");
        mKeyWordList.add("final");
        mKeyWordList.add("finally");
        mKeyWordList.add("float");
        mKeyWordList.add("for");
        mKeyWordList.add("goto");
        mKeyWordList.add("if");
        mKeyWordList.add("implements");
        mKeyWordList.add("import");
        mKeyWordList.add("instanceof");
        mKeyWordList.add("int");
        mKeyWordList.add("interface");
        mKeyWordList.add("long");
        mKeyWordList.add("native");
        mKeyWordList.add("new");
        mKeyWordList.add("package");
        mKeyWordList.add("private");
        mKeyWordList.add("protected");
        mKeyWordList.add("public");
        mKeyWordList.add("return");
        mKeyWordList.add("strictfp");
        mKeyWordList.add("short");
        mKeyWordList.add("static");
        mKeyWordList.add("super");
        mKeyWordList.add("switch");
        mKeyWordList.add("synchronized");
        mKeyWordList.add("this");
        mKeyWordList.add("throw");
        mKeyWordList.add("throws");
        mKeyWordList.add("transient");
        mKeyWordList.add("abstract");
        mKeyWordList.add("void");
        mKeyWordList.add("volatile");
        mKeyWordList.add("while");

        Set<String> set = json.keySet();

        List<String> fieldList = new ArrayList<String>();
        for (String key : set) {
            if (!mFilterFields.contains(key)) {
                fieldList.add(key);
            }
        }
        InnerClassEntity classInnerClassEntity = new InnerClassEntity();

        List<FieldEntity> fields = createField(classInnerClassEntity, json, fieldList, stringBuilder);
        FieldEntity fieldEntity = new FieldEntity();

        if (Config.getInstant().isFieldPrivateMode()) {
            createSetMethod(fields, stringBuilder, fieldEntity);
            createGetMethod(fields, stringBuilder);
        }
        stringBuilder.append("\n");
    }


    private List<FieldEntity> createField(InnerClassEntity innerClassEntity, JSONObject json, List<String> list, StringBuilder stringBuilder) {
        List<FieldEntity> fieldEntities = new ArrayList<FieldEntity>();
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < list.size(); i++) {

            String key = list.get(i);
            Object type = json.get(key);
            StringBuilder filedSb = new StringBuilder();

            String tabSpaceStr = "    ";
            if (checkKeyWord(key)) {
                //是关键字 使用注解
                filedSb.append(tabSpaceStr).append("@SerializedName(\"").append(key).append("\")\n");
                key = key + "X";
            } else {
                if (Config.getInstant().isUseSerializedName()) {
                    filedSb.append(tabSpaceStr).append("@SerializedName(\"").append(key).append("\")\n");
                }
            }
            if (Config.getInstant().isUseSerializedName()) {
                key = captureStringLeaveUnderscore(key);
            }

            String typeStr = typeByValue(innerClassEntity, stringBuilder, key, type);
            // 配置是否是private（生成成员变量）
            if (Config.getInstant().isFieldPrivateMode()) {
                filedSb.append(tabSpaceStr).append("private").append(typeStr).append(key).append(";\n");
            } else {
                filedSb.append(tabSpaceStr).append("public").append(typeStr).append(key).append(";\n");
            }
            String filedStr;
            if (i == 0) {
                filedStr = sb.append(filedSb.toString()).toString();
            } else {
                filedStr = filedSb.toString();
            }
            FieldEntity fieldEntity = new FieldEntity();
            fieldEntity.setField(key);
            fieldEntity.setType(typeStr);
            fieldEntities.add(fieldEntity);

            stringBuilder.append(filedStr).append("\n");
        }
        return fieldEntities;
    }

    public boolean checkKeyWord(String key) {

        return mKeyWordList.contains(key);
    }


    private String typeByValue(InnerClassEntity innerClassEntity, StringBuilder stringBuilder, String key, Object type) {

        String typeStr;
        if (type instanceof Boolean) {
            typeStr = " boolean ";
        } else if (type instanceof Integer) {
            typeStr = " int ";
        } else if (type instanceof Double) {
            typeStr = " double ";
        } else if (type instanceof Long) {
            typeStr = " long ";
        } else if (type instanceof String) {
            typeStr = " String ";
        } else if (type instanceof JSONObject) {

            typeStr = checkInnerClass((JSONObject) type);

            if (typeStr == null) {
                typeStr = " " + createClassSubName(innerClassEntity, stringBuilder, key, type) + " ";
                createClassSub(innerClassEntity, typeStr, (JSONObject) type, stringBuilder);
            } else {
                typeStr = " " + typeStr + " ";
            }

        } else if (type instanceof JSONArray) {

            typeStr = " java.util.List<" + createClassSubName(innerClassEntity, stringBuilder, key, type) + "> ";

        } else {
            typeStr = " String ";
        }
        return typeStr;
    }

    private String checkInnerClass(JSONObject jsonObject) {

        for (InnerClassEntity InnerClassEntity : mFilterClass) {
            Iterator<String> keys = jsonObject.keys();

            boolean had = true;
            while (keys.hasNext()) {
                String key = keys.next();
                if (!InnerClassEntity.getFields().contains(key)) {
                    had = false;
                    break;
                }
            }
            if (had) {

                if (InnerClassEntity.getPackName() != null) {
                    return InnerClassEntity.getPackName() + "." + InnerClassEntity.getClassName();
                } else {
                    return InnerClassEntity.getClassName();
                }
            }
        }
        return null;
    }


    private void createClassSub(InnerClassEntity parentC, String className, JSONObject json, StringBuilder sb) {

        sb.append("public static class ").append(className).append("{");
        Set<String> set = json.keySet();
        List<String> list = new ArrayList<String>(set);

        InnerClassEntity innerClassEntity = new InnerClassEntity();

        innerClassEntity.setClassName(className);
        if (parentC.getClassName() != null) {

            if (parentC.getPackName() == null) {
                innerClassEntity.setPackName(parentC.getClassName());
            } else {
                innerClassEntity.setPackName(parentC.getPackName() + "." + parentC.getClassName());
            }
        }


        List<FieldEntity> fields = createField(innerClassEntity, json, list, sb);
        if (Config.getInstant().isFieldPrivateMode()) {
            createSetMethod(fields, sb, null);
            createGetMethod(fields, sb);
        }
        sb.append("\n\n");
        innerClassEntity.setFields(list);

        mFilterClass.add(innerClassEntity);


        sb.append("}\n");
    }

    private String createClassSubName(InnerClassEntity innerClassEntity, StringBuilder sb, String key, Object o) {

        String name = "";
        if (o instanceof JSONObject) {

            if (key == null || key.equals("")) {
                return key;
            }
            String[] strings = key.split("_");
            StringBuilder stringBuilder = new StringBuilder();
            for (String string : strings) {
                stringBuilder.append(captureName(string));
            }

            name = stringBuilder.toString() + Config.getInstant().getSuffixStr();

        } else if (o instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) o;
            if (jsonArray.length() > 0) {
                Object item = jsonArray.get(0);
                name = listTypeByValue(innerClassEntity, sb, key, item);
            } else {
                name = "?";
            }
        }
        return name;
    }

    private String listTypeByValue(InnerClassEntity innerClassEntity, StringBuilder stringBuilder, String key, Object type) {

        String typeStr;


        if (type instanceof JSONObject) {

            typeStr = checkInnerClass((JSONObject) type);
            if (typeStr == null) {
                typeStr = createClassSubName(innerClassEntity, stringBuilder, key, type);
                createClassSub(innerClassEntity, typeStr, (JSONObject) type, stringBuilder);
            }

        } else if (type instanceof JSONArray) {
            typeStr = " java.util.List<" + createClassSubName(innerClassEntity, stringBuilder, key, type) + "> ";
        } else {


            typeStr = type.getClass().getSimpleName();


        }
        return typeStr;
    }

    public String captureName(String name) {
        if (name.length() == 0) {
            return "";
        }
        name = name.substring(0, 1).toUpperCase() + name.substring(1);

        return name;
    }

    public String captureStringLeaveUnderscore(String str) {
        if (str == null || "".equals(str)) {
            return str;
        }

        str = str.replaceAll("^_+", "");
        String[] strings = str.split("_");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(strings[0]);
        for (int i = 1; i < strings.length; i++) {
            stringBuilder.append(captureName(strings[i]));
        }
        return stringBuilder.toString();

    }

    private void createSetMethod(List<FieldEntity> fields, StringBuilder sb, FieldEntity fieldEntity) {
        for (FieldEntity field1 : fields) {
            String field = field1.getField();
            String typeStr = field1.getType();
            String method = "public void set" + captureName(field) + "( " + typeStr + " " + field + ") {   this." + field + " = " + field + ";} ";
            sb.append(method);
        }
    }

    private void createGetMethod(List<FieldEntity> fields, StringBuilder sb) {

        for (FieldEntity field1 : fields) {
            String field = field1.getField();

            String typeStr = field1.getType();

            if (typeStr.equals(" boolean ")) {

                String method = "public " + typeStr + "   is" + captureName(field) + "() {   return " + field + ";} ";
                sb.append(method);
            } else {

                String method = "public " + typeStr + "   get" + captureName(field) + "() {   return " + field + ";} ";
                sb.append(method);
            }

        }
    }

}
