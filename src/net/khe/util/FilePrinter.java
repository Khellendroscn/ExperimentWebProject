package net.khe.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by hyc on 2016/10/16.
 */
public class FilePrinter {
    private File target;//目标文件
    PrintWriter writer;
    public FilePrinter(File target)throws IOException{
        this.target = target;
        checkFile();
        writer = new PrintWriter(target);
    }

    public FilePrinter(String fileName)throws IOException{
        this.target = new File(fileName);
        checkFile();
        writer = new PrintWriter(target);
    }
    private void checkFile()throws IOException{
        //检查文件，如果文件不存在则创建它
        if(!target.exists()){//文件不存在
            File dir = target.getParentFile();//获取文件路径
            if(!dir.exists()){
                dir.mkdir();//创建路径
            }
            target.createNewFile();//创建文件
        }else if(!target.isFile()){//该文件或路径已存在
            throw new IOException("Target is not a file.");//如果目标不是文件，抛出异常
        }
    }

    public void print(Object obj){
        writer.print(obj);
    }
    public void println(Object obj){
        writer.println(obj);
    }
    public void println(){
        writer.println();
    }
    public void printf(String format,Object...objs){
        writer.printf(format,objs);
    }
    public void close(){
        writer.close();
    }
    public static void main(String[] args) {
        try{
            FilePrinter printer = new FilePrinter("./test/FilePrinter.txt");
            printer.println("Succeed!!!");
            printer.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
