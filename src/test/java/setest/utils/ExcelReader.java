package setest.utils;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;
import org.apache.poi.ss.usermodel.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author asahi
 * @version 1.0
 * @project Software-Testing-Homework
 * @description 读取excel文件中的测试用例
 * @date 2024/6/2 11:19:12
 */

public class ExcelReader {

    public static List<Map<String, Object>> readData(String filePath, int headRows) throws IOException {
        // 设置导入参数
        ImportParams params = new ImportParams();
        params.setHeadRows(headRows); // 设置头行数

        // 读取Excel内容
        List<Map<String, Object>> list = ExcelImportUtil.importExcel(new File(filePath), Map.class, params);

        // 移除以 "output" 开头且值为 null 的行
        list.removeIf(row -> {
            for (Map.Entry<String, Object> entry : row.entrySet()) {
                if (entry.getKey().startsWith("output") && entry.getValue() == null) {
                    return true;
                }
            }
            return false;
        });

        return list;
    }

    public static List<Map<String, Object>> readData(InputStream file, int headRows) throws Exception {
        // 设置导入参数
        ImportParams params = new ImportParams();
        params.setHeadRows(headRows); // 设置头行数

        // 读取Excel内容
        List<Map<String, Object>> list = ExcelImportUtil.importExcel(file, Map.class, params);

        // 移除以 "output" 开头且值为 null 的行
        list.removeIf(row -> {
            for (Map.Entry<String, Object> entry : row.entrySet()) {
                if (entry.getKey().startsWith("output") && entry.getValue() == null) {
                    return true;
                }
            }
            return false;
        });

        return list;
    }

    public static List<List<String>> readHeaders(String filePath, int headRows) throws IOException {
        List<List<String>> headers = new ArrayList<>();
        // 获取当前工作目录
        String workingDir = System.getProperty("user.dir");

        // 打印当前工作目录
        System.out.println("当前工作目录: " + workingDir);

        try (FileInputStream fis = new FileInputStream(new File(filePath));
             Workbook workbook = WorkbookFactory.create(fis)) {

            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 0; i < headRows; i++) {
                Row row = sheet.getRow(i);
                List<String> headerRow = new ArrayList<>();
                for (Cell cell : row) {
                    headerRow.add(cell.getStringCellValue());
                }
                headers.add(headerRow);
            }
        }

        return headers;
    }

    public static List<List<String>> readHeaders(InputStream file, int headRows) throws IOException {
        List<List<String>> headers = new ArrayList<>();

        try (Workbook workbook = WorkbookFactory.create(file)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 0; i < headRows; i++) {
                Row row = sheet.getRow(i);
                List<String> headerRow = new ArrayList<>();
                for (Cell cell : row) {
                    headerRow.add(cell.getStringCellValue());
                }
                headers.add(headerRow);
            }
        }

        return headers;
    }

//    public static List<String> readTitleRow(String filePath) throws IOException {
//        List<String> titleRow = new ArrayList<>();
//
//        try (FileInputStream fis = new FileInputStream(new File(filePath));
//             Workbook workbook = WorkbookFactory.create(fis)) {
//
//            Sheet sheet = workbook.getSheetAt(0);
//            Row row = sheet.getRow(0); // 读取第一行
//            for (Cell cell : row) {
//                titleRow.add(cell.getStringCellValue());
//            }
//        }
//
//        return titleRow;
//    }


    /**
     * @Author asahi
     * @Description 测试该部分代码用, 无实际意义
     * @Date 下午1:21 2024/6/2
     * @Param
     * @param args
     * @return
     **/
//    public static void main(String[] args) {
//        String filePath = "/home/asahi/project/Java/Software-Testing-Homework/src/main/java/test/cases/ex1.xlsx";
//        int headRows = 2; // 假设有2行头行
//
//        try {
////            // 读取标题行内容
////            List<String> titleRow = readTitleRow(filePath);
////            System.out.println("Title Row:");
////            System.out.println(titleRow);
//
//            // 读取头行内容
//            List<List<String>> headers = readHeaders(filePath, headRows);
//            System.out.println("Headers:");
//            for (List<String> headerRow : headers) {
//                System.out.println(headerRow);
//            }
//
//            // 读取数据内容
//            List<Map<String, Object>> data = readData(filePath, headRows);
//            System.out.println("Data:");
//            for (Map<String, Object> row : data) {
//                System.out.println(row);
//            }
//
//        } catch (IOException e) {
////            e.printStackTrace();
//        }
//    }
}
