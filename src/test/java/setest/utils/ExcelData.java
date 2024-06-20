package setest.utils;

import lombok.Getter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author asahi
 * @version 1.0
 * @project Software-Testing-Homework
 * @description 存储从ExcelReader中得到的数据, 并按照类型转换
 * @date 2024/6/2 13:18:34
 */

@Getter
public class ExcelData {
//    private final List<String> titleRow;
    private final List<List<String>> headers;
    private final List<Map<String, Object>> data;
    private final List<String> DataMap;
    private final int headRows = 2;

    public ExcelData(String filePath) throws IOException {
//        titleRow = ExcelReader.readTitleRow(filePath);
        headers = ExcelReader.readHeaders(filePath, headRows);
        data = ExcelReader.readData(filePath, headRows);

        List<String> tempMap = new ArrayList<>();
        for (int i = 0; i < headers.get(0).size(); i++) {
            String map = headers.get(headers.size() - 2).get(i) + "_" + headers.get(headers.size() - 1).get(i);
            tempMap.add(map);
        }
        DataMap = tempMap;
    }

    public ExcelData(InputStream file) throws Exception {
        // 复制输入流
        // 复制输入流
        InputStream[] copies = copyInputStream(file);
        headers = ExcelReader.readHeaders(copies[0], headRows);
        data = ExcelReader.readData(copies[1], headRows);
        print();

        List<String> tempMap = new ArrayList<>();
        for (int i = 0; i < headers.get(0).size(); i++) {
            String map = headers.get(headers.size() - 2).get(i) + "_" + headers.get(headers.size() - 1).get(i);
            tempMap.add(map);
        }
        DataMap = tempMap;
    }

    public static InputStream[] copyInputStream(InputStream originalStream) throws IOException {
        // 读取原始流的内容到字节数组中
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = originalStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        byte[] byteArray = outputStream.toByteArray();

        // 创建两个新的 ByteArrayInputStream 对象并返回
        InputStream copy1 = new ByteArrayInputStream(byteArray);
        InputStream copy2 = new ByteArrayInputStream(byteArray);
        return new InputStream[]{copy1, copy2};
    }

    public void print() {
//        // 读取标题行内容
//        System.out.println("Title Row:");
//        System.out.println(titleRow);

        // 读取头行内容
        System.out.println("Headers:");
        for (List<String> headerRow : headers) {
            System.out.println(headerRow);
        }

        // 读取数据内容
        System.out.println("Data:");
        for (Map<String, Object> row : data) {
            System.out.println(row);
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
//    }
}
