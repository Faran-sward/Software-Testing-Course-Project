package setest.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mockito.Mockito;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author asahi
 * @version 1.0
 * @project Software-Testing-Homework
 * @description 将Excel中读入的数据转化为相应格式
 * @date 2024/6/2 12:49:13
 */


public class DataConverter {
    public static List<MethodData> convertToMethodData(List<Map<String, Object>> dataList,
                                                       List<String> dataMap,
                                                       Class<?>[] parameterTypes,
                                                       Class<?> resultType) {
        List<MethodData> methodDataList = new ArrayList<>();

        for (Map<String, Object> data : dataList) {
            boolean flag = false;

            // 获取输入参数
            Object[] parameters = new Object[parameterTypes.length];
            for (int i = 0; i < parameterTypes.length; i++) {

                parameters[i] = convertToType(data.get(dataMap.get(i)), parameterTypes[i]);
                if (parameters[i] != null)
                    flag = true;
            }


            // 获取输出结果
            Object result = null;
            if (resultType != void.class)
                result = convertToType(data.get(dataMap.get(parameterTypes.length)), resultType);

            // 创建 MethodData 对象并添加到列表中
            if (flag)
                methodDataList.add(new MethodData(parameters, result));
        }

        return methodDataList;
    }

    private static Object convertToType(Object value, Class<?> type) {
        if (value == null) {
            return null;
        }

        ObjectMapper mapper = new ObjectMapper();

        try {
            if (type.isArray()) {
                // 获取数组的组件类型
                Class<?> componentType = type.getComponentType();
                if (componentType == MultipartFile.class) {
                    // 生成一个mock的MultipartFile数组
                    int length = Integer.parseInt(value.toString());
                    MultipartFile[] files = new MultipartFile[length];
                    for (int i = 0; i < length; i++) {
                        files[i] = Mockito.mock(MultipartFile.class);
                    }
                    return files;
                } else {
                    // 将 JSON 字符串转换为相应类型的数组
                    return mapper.readValue(value.toString(), mapper.getTypeFactory().constructArrayType(componentType));
                }
            }

            if (type == MultipartFile.class) {
                // 生成一个mock的MultipartFile
                return Mockito.mock(MultipartFile.class);
            } else if (type == Integer.class || type == int.class) {
                return Integer.parseInt(value.toString());
            } else if (type == Double.class || type == double.class) {
                return Double.parseDouble(value.toString());
            } else if (type == Boolean.class || type == boolean.class) {
                return Boolean.parseBoolean(value.toString());
            } else if (type == String.class) {
                return value.toString();
            } else if (type == BigDecimal.class) {
                return new BigDecimal(value.toString());
            } else if (type == Date.class) {
                return Date.valueOf(value.toString());
            } else {
                // 如果类型不是基本数据类型或字符串，可能需要根据具体情况进行转换
                return value;
            }
        } catch (Exception e) {
            System.err.println("Error converting value to type: " + type.getSimpleName());
            e.printStackTrace();
            return null;
        }
    }


    public static class MethodData {
        private Object[] parameters;
        private Object result;

        public MethodData(Object[] parameters, Object result) {
            this.parameters = parameters;
            this.result = result;
        }

        public Object[] getParameters() {
            return parameters;
        }

        public Object getResult() {
            return result;
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
//    public static void main(String[] args) throws IOException {
//        String filePath = "/home/asahi/project/Java/Software-Testing-Homework/src/main/java/test/cases/ex1.xlsx";
//
//        ExcelData excelData = new ExcelData(filePath);
//
//        excelData.print();
//
//        List<List<String>> headers = excelData.getHeaders();
//        List<String> dataMap = excelData.getDataMap();
//        List<Map<String, Object>> data = excelData.getData();
//
//        // 读取类型
//        List<String> lastRow = headers.get(headers.size() - 1);
//        List<String> input = lastRow.subList(0, lastRow.size() - 2);
//        String output = lastRow.get(lastRow.size() - 2);
//        System.out.println("Last Row (Parameter Types):");
//        System.out.println(input);
//
//        // 假设已经知道了输入类型 parameterTypes 和输出类型 resultType
//        Class<?>[] parameterTypes = TypeConverter.convertToClassList(input).toArray(new Class[0]); // 替换为实际的输入类型
//        Class<?> resultType = TypeConverter.convertToClass(output); // 替换为实际的输出类型
//
//        // 转换数据为相应格式
//        List<MethodData> methodDataList = convertToMethodData(data, dataMap, parameterTypes, resultType);
//
//        // 打印转换后的数据
//        for (MethodData methodData : methodDataList) {
//            System.out.println("Parameters: " + Arrays.toString(methodData.getParameters()));
//            System.out.println("Result: " + methodData.getResult());
//            System.out.println();
//        }
//    }
}

