package com.lcg.mylibrary;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * JSON 操作 工具类 还需要优化
 *
 * @author lei.chuguang Email:475825657@qq.com
 * @version 1.0
 * @since 2017/3/29 16:00
 */
public class JsonTool {

    private static boolean DEBUG = false;

    /**
     * 将JSON字符串封装到对象
     *
     * @param jsonStr 待封装的JSON字符串
     * @param clazz   待封装的实例字节码
     * @return T: 封装JSON数据的对象
     * @version 1.0
     */
    public static <T> T toBean(String jsonStr, Class<T> clazz) {
        return JSON.parseObject(jsonStr, clazz);
    }

    /**
     * 将 对象编码为 JSON格式
     *
     * @param t 待封装的对象
     * @return String: 封装后JSONObject String格式
     * @version 1.0
     */
    public static <T> String toJson(T t) {
        if (t == null) {
            return "{}";
        }
        return objectToJson(t);
    }

    /**
     * 由JSON字符串生成Bean对象
     *
     * @param jsonStr
     * @param className 待生成Bean对象的名称
     * @return String:
     * @version 1.0
     */
    public static String createBean(String jsonStr, String className) {
        JSONObject job = JSON.parseObject(jsonStr);
        return createObject(job, className, 0);
    }

    /**
     * JSONObject 封装到 对象实例
     *
     * @param job 待封装的JSONObject
     * @param c   待封装的实例对象class
     * @param v   待封装实例的外部类实例对象</br>只有内部类存在,外部类时传递null
     * @return T:封装数据的实例对象
     * @version 1.0
     * @date 2015-10-9
     * @Author zhou.wenkai
     */
    @SuppressWarnings("unchecked")
    private static <T, V> T parseObject(JSONObject job, Class<T> c, V v) {
        T t = null;
        try {
            if (null == v) {
                t = c.newInstance();
            } else {
                Constructor<?> constructor = c.getDeclaredConstructors()[0];
                constructor.setAccessible(true);
                t = (T) constructor.newInstance(v);
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            if (DEBUG)
                e.printStackTrace();
        }

        Field[] fields = c.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            Class<?> type = field.getType();
            String name = field.getName();

            // if the object don`t has a mapping for name, then continue
            if (!job.containsKey(name)) continue;

            String typeName = type.getName();
            if (typeName.equals("java.lang.String")) {
                try {
                    String value = job.getString(name);
                    if (value != null && value.equals("null")) {
                        value = "";
                    }
                    field.set(t, value);
                } catch (Exception e) {
                    if (DEBUG)
                        e.printStackTrace();
                    try {
                        field.set(t, "");
                    } catch (Exception e1) {
                        if (DEBUG)
                            e1.printStackTrace();
                    }
                }
            } else if (typeName.equals("int") ||
                    typeName.equals("java.lang.Integer")) {
                try {
                    field.set(t, job.getIntValue(name));
                } catch (Exception e) {
                    if (DEBUG)
                        e.printStackTrace();
                }
            } else if (typeName.equals("boolean") ||
                    typeName.equals("java.lang.Boolean")) {
                try {
                    field.set(t, job.getBoolean(name));
                } catch (Exception e) {
                    if (DEBUG)
                        e.printStackTrace();
                }
            } else if (typeName.equals("float") ||
                    typeName.equals("java.lang.Float")) {
                try {
                    field.set(t, Float.valueOf(job.getString(name)));
                } catch (Exception e) {
                    if (DEBUG)
                        e.printStackTrace();
                }
            } else if (typeName.equals("double") ||
                    typeName.equals("java.lang.Double")) {
                try {
                    field.set(t, job.getDouble(name));
                } catch (Exception e) {
                    if (DEBUG)
                        e.printStackTrace();
                }
            } else if (typeName.equals("long") ||
                    typeName.equals("java.lang.Long")) {
                try {
                    field.set(t, job.getLong(name));
                } catch (Exception e) {
                    if (DEBUG)
                        e.printStackTrace();
                }
            } else if (typeName.equals("java.util.List") ||
                    typeName.equals("java.util.ArrayList")) {
                try {
                    Object obj = job.get(name);
                    Type genericType = field.getGenericType();
                    String className = genericType.toString().replace("<", "")
                            .replace(type.getName(), "").replace(">", "");
                    Class<?> clazz = Class.forName(className);
                    if (obj instanceof JSONArray) {
                        ArrayList<?> objList = parseArray((JSONArray) obj, clazz, t);
                        field.set(t, objList);
                    }
                } catch (Exception e) {
                    if (DEBUG)
                        e.printStackTrace();
                }
            } else {
                try {
                    Object obj = job.get(name);
                    Class<?> clazz = Class.forName(typeName);
                    if (obj instanceof JSONObject) {
                        Object parseJson = parseObject((JSONObject) obj, clazz, t);
                        field.set(t, parseJson);
                    }
                } catch (Exception e) {
                    if (DEBUG)
                        e.printStackTrace();
                }

            }
        }

        return t;
    }

    /**
     * 将 JSONArray 封装到 ArrayList 对象
     *
     * @param array 待封装的JSONArray
     * @param c     待封装实体字节码
     * @param v     待封装实例的外部类实例对象</br>只有内部类存在,外部类时传递null
     * @return ArrayList<T>: 封装后的实体集合
     * @version 1.0
     * @date 2015-10-8
     */
    @SuppressWarnings("unchecked")
    private static <T, V> ArrayList<T> parseArray(JSONArray array, Class<T> c, V v) {
        ArrayList<T> list = new ArrayList<T>(array.size());
        try {
            for (int i = 0; i < array.size(); i++) {
                if (array.get(i) instanceof JSONObject) {
                    T t = parseObject(array.getJSONObject(i), c, v);
                    list.add(t);
                } else {
                    list.add((T) array.get(i));
                }

            }
        } catch (Exception e) {
            if (DEBUG)
                e.printStackTrace();
        }
        return list;
    }

    /**
     * 将 对象编码为 JSON格式
     *
     * @param t 待封装的对象
     * @return String: 封装后JSONObject String格式
     * @version 1.0
     * @date 2015-10-11
     * @Author zhou.wenkai
     */
    private static <T> String objectToJson(T t) {

        Field[] fields = t.getClass().getDeclaredFields();
        StringBuilder sb = new StringBuilder(fields.length << 4);
        sb.append("{");

        for (Field field : fields) {
            field.setAccessible(true);
            Class<?> type = field.getType();
            String name = field.getName();

            // 'this$Number' 是内部类的外部类引用(指针)字段
            if (name.contains("this$")) continue;

            String typeName = type.getName();
            if (typeName.equals("java.lang.String")) {
                try {
                    sb.append("\"" + name + "\":");
                    sb.append(stringToJson((String) field.get(t)));
                    sb.append(",");
                } catch (Exception e) {
                    if (DEBUG)
                        e.printStackTrace();
                }
            } else if (typeName.equals("boolean") ||
                    typeName.equals("java.lang.Boolean") ||
                    typeName.equals("int") ||
                    typeName.equals("java.lang.Integer") ||
                    typeName.equals("float") ||
                    typeName.equals("java.lang.Float") ||
                    typeName.equals("double") ||
                    typeName.equals("java.lang.Double") ||
                    typeName.equals("long") ||
                    typeName.equals("java.lang.Long")) {
                try {
                    sb.append("\"" + name + "\":");
                    sb.append(field.get(t));
                    sb.append(",");
                } catch (Exception e) {
                    if (DEBUG)
                        e.printStackTrace();
                }
            } else if (typeName.equals("java.util.List") ||
                    typeName.equals("java.util.ArrayList")) {
                try {
                    List<?> objList = (List<?>) field.get(t);
                    if (null != objList && objList.size() > 0) {
                        sb.append("\"" + name + "\":");
                        sb.append("[");
                        String toJson = listToJson((List<?>) field.get(t));
                        sb.append(toJson);
                        sb.setCharAt(sb.length() - 1, ']');
                        sb.append(",");
                    }
                } catch (Exception e) {
                    if (DEBUG)
                        e.printStackTrace();
                }
            } else {
                try {
                    sb.append("\"" + name + "\":");
                    sb.append("{");
                    sb.append(objectToJson(field.get(t)));
                    sb.setCharAt(sb.length() - 1, '}');
                    sb.append(",");
                } catch (Exception e) {
                    if (DEBUG)
                        e.printStackTrace();
                }
            }

        }
        if (sb.length() == 1) {
            sb.append("}");
        }
        sb.setCharAt(sb.length() - 1, '}');
        return sb.toString();
    }

    /**
     * 将 List 对象编码为 JSON格式
     *
     * @param objList 待封装的对象集合
     * @return String:封装后JSONArray String格式
     * @version 1.0
     * @date 2015-10-11
     * @Author zhou.wenkai
     */
    private static <T> String listToJson(List<T> objList) {
        final StringBuilder sb = new StringBuilder();
        for (T t : objList) {
            if (t instanceof String) {
                sb.append(stringToJson((String) t));
                sb.append(",");
            } else if (t instanceof Boolean ||
                    t instanceof Integer ||
                    t instanceof Float ||
                    t instanceof Double) {
                sb.append(t);
                sb.append(",");
            } else {
                sb.append(objectToJson(t));
                sb.append(",");
            }
        }
        return sb.toString();
    }

    /**
     * 将 String 对象编码为 JSON格式，只需处理好特殊字符
     *
     * @param str String 对象
     * @return String:JSON格式
     * @version 1.0
     * @date 2015-10-11
     * @Author zhou.wenkai
     */
    private static String stringToJson(final String str) {
        if (str == null || str.length() == 0) {
            return "\"\"";
        }
        final StringBuilder sb = new StringBuilder(str.length() + 2 << 4);
        sb.append('\"');
        for (int i = 0; i < str.length(); i++) {
            final char c = str.charAt(i);

            sb.append(c == '\"' ? "\\\"" : c == '\\' ? "\\\\"
                    : c == '/' ? "\\/" : c == '\b' ? "\\b" : c == '\f' ? "\\f"
                    : c == '\n' ? "\\n" : c == '\r' ? "\\r"
                    : c == '\t' ? "\\t" : c);
        }
        sb.append('\"');
        return sb.toString();
    }

    /**
     * 由JSONObject生成Bean对象
     *
     * @param job
     * @param className 待生成Bean对象的名称
     * @param outCount  外部类的个数
     * @return LinkedList<String>: 生成的Bean对象
     * @version 1.0
     * @date 2015-10-16
     * @Author zhou.wenkai
     */
    private static String createObject(JSONObject job, String className, int outCount) {
        final StringBuilder sb = new StringBuilder();
        String separator = System.getProperty("line.separator");

        // 生成的Bean类前部的缩进空间
        String classFrontSpace = "";
        // 生成的Bean类字段前部的缩进空间
        String fieldFrontSpace = "    ";
        for (int i = 0; i < outCount; i++) {
            classFrontSpace += "    ";
            fieldFrontSpace += "    ";
        }

        sb.append(classFrontSpace + "public class " + className + " {");

        Iterator<String> it = job.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            Object obj = job.get(key);
            if (obj instanceof JSONArray) {
                // 判断类是否为基本数据类型,如果为自定义类则字段类型取将key的首字母大写作为内部类名称
                String fieldType = ((JSONArray) obj).get(0) instanceof JSONObject ?
                        "" : ((JSONArray) obj).get(0).getClass().getSimpleName();
                if (fieldType == "") {
                    fieldType = String.valueOf(Character.isUpperCase(key.charAt(0)) ?
                            key.charAt(0) : Character.toUpperCase(key.charAt(0))) + key
                            .substring(1);
                }
                sb.append(separator);
                sb.append(fieldFrontSpace + "public List<" + fieldType + "> " + key + ";");

                // 如果字段类型为自定义类类型,则取JSONArray中第一个JSONObject生成Bean
                if (((JSONArray) obj).get(0) instanceof JSONObject) {
                    sb.append(separator);
                    sb.append(separator);
                    sb.append(fieldFrontSpace + "/** " + fieldType + " is the inner class of " +
                            "" + className + " */");
                    sb.append(separator);
                    sb.append(createObject((JSONObject) ((JSONArray) obj).get(0), fieldType,
                            outCount + 1));
                }
            } else if (obj instanceof JSONObject) {
                String fieldType = String.valueOf(Character.isUpperCase(key.charAt(0)) ?
                        key.charAt(0) : Character.toUpperCase(key.charAt(0))) + key.substring
                        (1);
                sb.append(separator);
                sb.append(fieldFrontSpace + "public List<" + fieldType + "> " + key + ";");
                sb.append(separator);
                sb.append(separator);
                sb.append(fieldFrontSpace + "/** " + fieldType + " is the inner class of " +
                        className + " */");
                sb.append(separator);
                sb.append(createObject((JSONObject) obj, fieldType, outCount + 1));
            } else {
                String type = obj.getClass().getSimpleName();
                type = type.replace("Boolean", "boolean").replace("Integer", "int").replace
                        ("Float", "float").replace("Double", "double").replace("Long", "long");
                sb.append(separator);
                sb.append(fieldFrontSpace + "public " + type + " " + key + ";");
            }
        }

        sb.append(separator);
        sb.append(classFrontSpace + "}");
        sb.append(separator);

        return sb.toString();
    }

    public static void main(String[] args) {
        String s = "{\"id\":1,\"coutlines\":[{\"id\":1," +
                "\"groupName\":\"春季上\",\"maxNum\":7,\"total\":9,\"rtype\":1," +
                "\"chapters\":[{\"id\":\"00000100000000000000\",\"chapterName\":\"一元一次方程\"," +
                "\"n\":2,\"courseType\":1,\"status\":0,\"exam\":0}]}],\"courses\":[{\"id\":1," +
                "\"groupName\":\"春季上\",\"total\":9,\"rtype\":1,\"courses\":[{\"id\":1," +
                "\"courseType\":1,\"status\":0,\"desc\":[\"一元一次方程\"],\"exam\":0," +
                "\"previewUrl\":\"http://xxx\"}]}]}";
        String beanStr = JsonTool.createBean(s, "TestBean");
        System.out.println(beanStr);
    }
}

