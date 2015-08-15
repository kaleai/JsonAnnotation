package com.jsonformat;

import com.sun.istack.internal.NotNull;

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
        mKeyWordList = new ArrayList<>();
        mFilterFields = new ArrayList<>();
        mFilterClass = new ArrayList<>();
    }

    public interface ParseListener {

        void onParseComplete(String str);

        void onParseError(Exception e);
    }

    public void parse(@NotNull String jsonStr, @NotNull String rootClassName, @NotNull ParseListener listener) {
        mSB.append("public class ").append(rootClassName).append(" {");
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

        if (json != null) {
            try {
                //mFilterFields = initFilterField(mClass);
                initFilterClass();
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

    private void initFilterClass() {

//        PsiClass[] psiClasses = this.mClass.getAllInnerClasses();
//        for (PsiClass psiClass : psiClasses) {
//
//            InnerClassEntity innerClassEntity1 = new InnerClassEntity();
//            innerClassEntity1.setClassName(psiClass.getName());
//            innerClassEntity1.setFields(initFilterField(psiClass));
//            innerClassEntity1.setPackName("");
//            innerClassEntity1.setPsiClass(psiClass);
//            recursionInnerClass(innerClassEntity1);
//        }
    }

//    private void recursionInnerClass(InnerClassEntity innerClassEntity) {
//        PsiClass[] innerClassｓ = innerClassEntity.getPsiClass().getInnerClasses();
//        if (innerClassｓ.length == 0) {
//
//            mFilterClass.add(innerClassEntity);
//        } else {
//            for (PsiClass psiClass : innerClassｓ) {
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

//    public List<String> initFilterField(PsiClass mClass) {
//
//        PsiField[] psiFields = mClass.getAllFields();
//        ArrayList<String> filterFields = new ArrayList<String>();
//        for (PsiField psiField : psiFields) {
//            String psiFieldText = filterAnnotation(psiField.getText());
//            if (psiFieldText.contains("SerializedName")) {
//                boolean isSerializedName = false;
//
//                psiFieldText = psiFieldText.trim();
//
//                Pattern pattern = Pattern.compile("@com\\s*\\.\\s*google\\s*\\.\\s*gson\\s*\\.\\s*annotations\\s*\\.\\s*SerializedName\\s*\\(\\s*\"(\\w+)\"\\s*\\)");
//                Matcher matcher = pattern.matcher(psiFieldText);
//                if (matcher.find()) {
//                    filterFields.add(matcher.group(1));
//                    isSerializedName = true;
//                }
//                Pattern pattern2 = Pattern.compile("@\\s*SerializedName\\s*\\(\\s*\"(\\w+)\"\\s*\\)");
//                Matcher matcher2 = pattern2.matcher(psiFieldText);
//                if (matcher2.find()) {
//                    filterFields.add(matcher2.group(1));
//                    isSerializedName = true;
//                }
//                if (!isSerializedName) {
//                    filterFields.add(psiField.getName());
//                }
//            } else {
//                filterFields.add(psiField.getName());
//            }
//        }
//
//        return filterFields;
//
//
//    }

    public void parseJson(JSONObject json, StringBuilder stringBuilder) {
        mKeyWordList.add("default");
        mKeyWordList.add("public");
        mKeyWordList.add("abstract");
        mKeyWordList.add("null");
        mKeyWordList.add("final");
        mKeyWordList.add("void");
        mKeyWordList.add("implements");
        mKeyWordList.add("this");
        mKeyWordList.add("instanceof");
        mKeyWordList.add("native");
        mKeyWordList.add("new");
        mKeyWordList.add("goto");
        mKeyWordList.add("const");
        mKeyWordList.add("volatile");
        mKeyWordList.add("return");
        mKeyWordList.add("finally");

        Set<String> set = json.keySet();

        List<String> fieldList = new ArrayList<>();
        for (String key : set) {
            if (!mFilterFields.contains(key)) {
                fieldList.add(key);
            }
        }

        List<FieldEntity> fields = createField(json, fieldList, stringBuilder);

        if (Config.getInstant().isFieldPrivateMode()) {
            createSetMethod(fields, stringBuilder);
            createGetMethod(fields, stringBuilder);
        }
    }


    private List<FieldEntity> createField(JSONObject json, List<String> list, StringBuilder stringBuilder) {
        List<FieldEntity> fieldEntities = new ArrayList<FieldEntity>();
        StringBuilder sb = new StringBuilder();
        sb.append("/** \n");
        for (String key : list) {
            sb.append("* ").append(key).append(" : ").append(json.get(key)).append("\n");
        }
        sb.append("*/ \n");

        for (int i = 0; i < list.size(); i++) {

            String key = list.get(i);
            Object type = json.get(key);
            StringBuilder filedSb = new StringBuilder();

            if (checkKeyWord(key)) {
                //是关键字 使用注解
                filedSb.append("@com.google.gson.annotations.SerializedName(\"").append(key).append("\")\n");
                key = key + "X";
            } else {
                if (Config.getInstant().isUseSerializedName()) {
                    filedSb.append("@com.google.gson.annotations.SerializedName(\"").append(key).append("\")\n");
                }
            }
            if (Config.getInstant().isUseSerializedName()) {
                key = captureStringLeaveUnderscore(key);
            }

            String typeStr = typeByValue(stringBuilder, key, type);
            // 配置是否是private
            if (Config.getInstant().isFieldPrivateMode()) {
                filedSb.append("private  ").append(typeStr).append(key).append(" ; ");
            } else {
                filedSb.append("public  ").append(typeStr).append(key).append(" ; ");
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


    private String typeByValue(StringBuilder stringBuilder, String key, Object type) {

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
        } else if (type instanceof Character) {
            typeStr = " char ";
        } else if (type instanceof JSONObject) {

            typeStr = checkInnerClass((JSONObject) type);

            if (typeStr == null) {
                typeStr = " " + createClassSubName(stringBuilder, key, type) + " ";
                createClassSub(typeStr, (JSONObject) type, stringBuilder);
            } else {
                typeStr = " " + typeStr + " ";
            }

        } else if (type instanceof JSONArray) {
            typeStr = " java.util.List<" + createClassSubName(stringBuilder, key, type) + "> ";
        } else {
            typeStr = " String ";
        }
        return typeStr;
    }

    private String checkInnerClass(JSONObject jsonObject) {

        for (InnerClassEntity innerClassEntity : mFilterClass) {
            Iterator<String> keys = jsonObject.keys();

            boolean had = true;
            while (keys.hasNext()) {
                String key = keys.next();
                if (!innerClassEntity.getFields().contains(key)) {
                    had = false;
                    break;
                }
            }
            if (had) {
                return innerClassEntity.getPackName() + innerClassEntity.getClassName();
            }
        }
        return null;
    }


    private void createClassSub(String className, JSONObject json, StringBuilder sb) {
        sb.append("public static class ").append(className).append("{");
//        PsiClass subClass = mFactory.createClassFromText(classContent, null).getInnerClasses()[0];
        Set<String> set = json.keySet();
        List<String> list = new ArrayList<String>(set);

        List<FieldEntity> fields = createField(json, list, sb);
        if (Config.getInstant().isFieldPrivateMode()) {
            createSetMethod(fields, sb);
            createGetMethod(fields, sb);
        }

        InnerClassEntity innerClassEntity = new InnerClassEntity();
//       innerClassEntity.setClassName(subClass.getName());
//        innerClassEntity.setPackName(mClass.getName()+".");
        innerClassEntity.setFields(list);

        mFilterClass.add(innerClassEntity);

        sb.append("}");
    }

    private String createClassSubName(StringBuilder sb, String key, Object o) {

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
                name = typeByValue(sb, key, item);
            } else {
                name = "?";
            }
        }
        return name;

    }


    public String captureName(String name) {

        name = name.substring(0, 1).toUpperCase() + name.substring(1);

        return name;
    }

    public String captureStringLeaveUnderscore(String str) {
        if (str == null || "".equals(str)) {
            return str;
        }
        String[] strings = str.split("_");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(strings[0]);
        for (int i = 1; i < strings.length; i++) {
            stringBuilder.append(captureName(strings[i]));
        }

        return stringBuilder.toString();

    }

    private void createSetMethod(List<FieldEntity> fields, StringBuilder sb) {
        for (FieldEntity field1 : fields) {
            String field = field1.getField();
            String typeStr = field1.getType();
            String method = "public void  set" + captureName(field) + "( " + typeStr + " " + field + ") {   this." + field + " = " + field + ";} ";
            sb.append(method);
        }
    }

    private void createGetMethod(List<FieldEntity> fields, StringBuilder sb) {

        for (FieldEntity field1 : fields) {
            String field = field1.getField();

            String typeStr = field1.getType();

            if (typeStr.equals(" boolean ")) {

                String method = "public " + typeStr + "   is" + captureName(field) + "() {   return " + field + " ;} ";
                sb.append(method);
            } else {

                String method = "public " + typeStr + "   get" + captureName(field) + "() {   return " + field + " ;} ";
                sb.append(method);
            }


        }

    }
//    private void createMethod(String method, PsiClass cla) {
//
//        cla.add(mFactory.createMethodFromText(method, cla));
//
//    }
}
