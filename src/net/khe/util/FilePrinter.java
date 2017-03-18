package net.khe.util;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by hyc on 2016/10/16.
 */
public class FilePrinter {
    private File target;//Ŀ���ļ�
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
        //����ļ�������ļ��������򴴽���
        if(!target.exists()){//�ļ�������
            File dir = target.getParentFile();//��ȡ�ļ�·��
            if(!dir.exists()){
                dir.mkdir();//����·��
            }
            target.createNewFile();//�����ļ�
        }else if(!target.isFile()){//���ļ���·���Ѵ���
            throw new IOException("Target is not a file.");//���Ŀ�겻���ļ����׳��쳣
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
