package edu.tongji.setest.utils.testCase;

import edu.tongji.setest.utils.DataConverter;
import edu.tongji.setest.utils.ExcelData;
import edu.tongji.setest.utils.TypeConverter;
import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author asahi
 * @version 1.0
 * @project Software-Testing-Homework
 * @description 存储从Excel表中转换来的测试用例信息
 * @date 2024/6/2 13:37:29
 */
@Getter
public class TestCase {
//    private final String methodName;
//    private final String className;
    private final List<String> paramsTypeName;
    private final String resultTypeName;
    private final Class<?>[] parameterTypes;
    private final Class<?> resultType;
    private final List<DataConverter.MethodData> DataList;

    public static void removeTrailingEmptyStrings(List<String> list) {
        int firstEmptyIndex = -1;

        // 找到第一个为空的字符串的索引
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isEmpty()) {
                firstEmptyIndex = i;
                break;
            }
        }

        // 如果找到了第一个为空的字符串
        if (firstEmptyIndex != -1) {
            // 从第一个空字符串开始，删除所有空字符串
            for (int i = firstEmptyIndex; i < list.size(); ) {
                if (list.get(i).isEmpty()) {
                    list.remove(i);
                } else {
                    i++;
                }
            }
        }
    }

    public TestCase (String filePath) throws IOException {
        ExcelData excelData = new ExcelData(filePath);

//        // 读取待测试对象名称
//        List<String> titleRow = excelData.getTitleRow();
//        if (titleRow.size() > 1) {
//            methodName = titleRow.get(1);
//        }
//        else{
//            methodName = titleRow.get(0);
//        }
//        className = titleRow.get(0);

        // 读取类型
        List<List<String>> headers = excelData.getHeaders();
        List<String> lastRow = headers.get(headers.size() - 1);

        // 调用方法删除第一个空字符串及其后面的所有空字符串
        removeTrailingEmptyStrings(lastRow);

        if ("comment".equalsIgnoreCase(lastRow.get(lastRow.size() - 1))){  // 以comment结尾
            if ("output".equalsIgnoreCase(lastRow.get(lastRow.size() - 2))) {  // 且返回值不为void
                paramsTypeName = lastRow.subList(0, lastRow.size() - 2);
                resultTypeName = lastRow.get(lastRow.size() - 2);
            }
            else {
                // subList 不包括结束索引的元素
                paramsTypeName = lastRow.subList(0, lastRow.size() - 1);  // 返回值为void
                resultTypeName = "void";
            }
        }
        else {  // 既没有output 也没有 comment ， 默认为无返回值函数
            paramsTypeName = lastRow;
            resultTypeName = "void";
        }


        // 类型转换
        parameterTypes = TypeConverter.convertToClassList(paramsTypeName).toArray(new Class[0]);
        resultType = TypeConverter.convertToClass(resultTypeName);

        // 数据类型转换
        List<Map<String, Object>> data = excelData.getData();
        List<String> DataMap = excelData.getDataMap();
        DataList = DataConverter.convertToMethodData(data, DataMap, parameterTypes, resultType);
    }

    public TestCase (InputStream file) throws Exception {
        ExcelData excelData = new ExcelData(file);

        // 读取类型
        List<List<String>> headers = excelData.getHeaders();
        List<String> lastRow = headers.get(headers.size() - 1);
        paramsTypeName = lastRow.subList(0, lastRow.size() - 2);
        resultTypeName = lastRow.get(lastRow.size() - 2);

        // 类型转换
        parameterTypes = TypeConverter.convertToClassList(paramsTypeName).toArray(new Class[0]);
        resultType = TypeConverter.convertToClass(resultTypeName);

        // 数据类型转换
        List<Map<String, Object>> data = excelData.getData();
        List<String> DataMap = excelData.getDataMap();
        DataList = DataConverter.convertToMethodData(data, DataMap, parameterTypes, resultType);
    }

    public void print () {
//        System.out.println("Class Name:");
//        System.out.println(className);
//        System.out.println("Method Name:");
//        System.out.println(methodName);

        System.out.println("Parameter Types:");
        System.out.println(paramsTypeName);
        System.out.println("Result Type:");
        System.out.println(resultTypeName);

        // 打印转换后的数据
        for (DataConverter.MethodData methodData : DataList) {
            System.out.println("Parameters: " + Arrays.toString(methodData.getParameters()));
            System.out.println("Result: " + methodData.getResult());
            System.out.println();
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
//        String filePath = "/home/asahi/project/Java/Software-Testing-Homework/cases/ex1.xlsx";
//
//        TestCase testCase = new TestCase(filePath);
//
//        testCase.print();
//    }
}
