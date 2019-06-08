package com.ghl.wuhan.secondhand.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * 项目名称：com.ghl.wuhan.secondhand.util
 * 类描述：
 * 创建人：Liting
 * 创建时间：2019/6/9 9:25
 * 修改人：Liting
 * 修改时间：2019/6/9 9:25
 * 修改备注：
 * 版本：
 */

public class FileUtils {

    /**
     * 获得指定文件的byte数组
     */
    public static byte[] getBytes(String filePath){
        byte[] buffer = null;
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1024);
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }
}
