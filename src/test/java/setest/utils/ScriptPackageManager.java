package setest.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;

public class ScriptPackageManager {
    public static InputStream packageCheck(MultipartFile file, String desiredPackage) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream));

        boolean packageFound = false;
        String line;

        while ((line = reader.readLine()) != null) {
            if (line.startsWith("package ")) {
                packageFound = true;
                // 将找到的package修改为指定位置
                writer.write("package " + desiredPackage + ";\n");
            } else {
                writer.write(line + "\n");
            }
        }

        // 如果文件中没有package，则在开头添加指定的package
        if (!packageFound) {
            writer.write("package " + desiredPackage + ";\n");
        }

        reader.close();
        writer.close();

        // 将输出的字节数组转换为InputStream
        return new ByteArrayInputStream(outputStream.toByteArray());
    }
}
