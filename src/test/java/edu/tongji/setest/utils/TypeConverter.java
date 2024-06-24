package edu.tongji.setest.utils;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
/**
 * @author asahi
 * @version 1.0
 * @project Software-Testing-Homework
 * @description 将读取的类型字符串, 转化为类型本身
 * @date 2024/6/2 12:38:01
 */


public class TypeConverter {
    public static List<Class<?>> convertToClassList(List<String> typeList) {
        List<Class<?>> classList = new ArrayList<>();

        for (String typeName : typeList) {
            Class<?> clazz = getClassForTypeName(typeName);
            if (clazz != null) {
                classList.add(clazz);
            }
        }

        return classList;
    }

    public static Class<?> convertToClass(String typeName) {
        return getClassForTypeName(typeName);
    }

    private static Class<?> getClassForTypeName(String typeName) {
        // 检查是否为数组类型
        if (typeName.endsWith("[]")) {
            String elementTypeName = typeName.substring(0, typeName.length() - 2); // 移除 "[]"
            Class<?> elementClass = getClassForTypeName(elementTypeName); // 获取元素类型的 Class 对象
            if (elementClass != null) {
                // 通过 Array.newInstance 创建数组的 Class 对象
                return java.lang.reflect.Array.newInstance(elementClass, 0).getClass();
            } else {
                return null;
            }
        }

        // 处理基本类型和 String
        switch (typeName) {
            case "int":
            case "int ":
                return int.class;
            case "byte":
            case "byte ":
                return byte.class;
            case "short":
            case "short ":
                return short.class;
            case "long":
            case "long ":
                return long.class;
            case "float":
            case "float ":
                return float.class;
            case "double":
            case "double ":
                return double.class;
            case "char":
            case "char ":
                return char.class;
            case "boolean":
            case "boolean ":
            case "bool":
            case "bool ":
                return boolean.class;
            case "String":
            case "String ":
            case "string":
            case "string ":
                return String.class;
            case "Integer":
            case "Integer ":
                return Integer.class;
            case "void":
            case "void ":
            case "Void":
                return void.class;
            case "MultipartFile":
                return MultipartFile.class;
            case "Date":
                return Date.class;  // java.sql
            case "BigDecimal":
                return BigDecimal.class;
            default:
                try {
                    // 尝试获取指定名称的类
                    return Class.forName(typeName);
                } catch (ClassNotFoundException e) {
                    // 如果找不到对应的类，则输出错误信息并返回null
                    System.err.println("Class not found for type name: " + typeName);
                    return null;
                }
        }

    }

    /**
     * @Author asahi
     * @Description 测试该部分代码用, 无实际意义
     * @Date 下午1:21 2024/6/2
     * @Param
     * @param args
     * @return
     **/
    public static void main(String[] args) {
        List<String> typeList = new ArrayList<>();
        typeList.add("int");
        typeList.add("String");
        typeList.add("int");

        List<Class<?>> classList = convertToClassList(typeList);
        System.out.println("Converted class list: " + classList);
    }
}

