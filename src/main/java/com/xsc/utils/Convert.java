package com.xsc.utils;

import java.io.*;
import java.util.Arrays;

public class Convert {
    private Convert() {

    }

    public static byte[] fileConvertByteArray(String FileName) {
        byte[] bytes = {};
        // 后续优化
        FileInputStream inputStream;
        try {
            inputStream =
                    new FileInputStream("D:\\javaDemo\\SpringBoot\\SpringPractice\\src\\main\\resources\\static\\image\\wallhaven.jpg");
            int len = 0;
            byte[] byteA = new byte[1024];
            while (len != -1) {
                len = inputStream.read(byteA);
                if (len != -1)
                    byteA = Arrays.copyOf(byteA, len);// 拷贝数组，读取长度
                bytes = ArrayUtil.merge(bytes, byteA);
            }
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public static void byteArrayConvertFile(byte[] bytes, String fileName) {
        // 后续优化
        FileOutputStream outputStream;
        try {
            outputStream =
                    new FileOutputStream("D:\\javaDemo\\SpringBoot\\SpringPractice\\src\\main\\resources\\" + fileName);
            outputStream.write(bytes);
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static byte[] inputStreamConvertByteArray(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buff = new byte[1024];
        int rc = 0;
        while ((rc = inputStream.read(buff)) > 0) {
            byteArrayOutputStream.write(buff, 0, rc);
        }
        return byteArrayOutputStream.toByteArray();
    }

}
