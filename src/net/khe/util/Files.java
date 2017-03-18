package net.khe.util;

import java.io.File;
import java.io.IOException;

/**
 * Created by hyc on 2016/12/22.
 */
public class Files {
    public static void createFileIfNotExists(File file) throws IOException {
        //检查文件，如果文件不存在则创建它
        if(!file.exists()){//文件不存在
            File dir = file.getParentFile();//获取文件路径
            if(!dir.exists()){
                dir.mkdir();//创建路径
            }
            file.createNewFile();//创建文件
        }else if(!file.isFile()){//该文件或路径已存在
            throw new IOException("Target is not a file.");//如果目标不是文件，抛出异常
        }
    }
}
