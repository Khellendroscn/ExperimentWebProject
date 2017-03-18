package net.khe.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by hyc on 2016/10/17.
 */
public class TextFileReader implements Iterable<String>{
    private ArrayList<String> content = new ArrayList<>();
    private File target;
    private BufferedReader reader;
    private void checkFile() throws IOException{
        if(!target.exists()) throw new IOException("Target file is not exists");
        if(!target.isFile()) throw new IOException("Target is not a file");
    }
    private void read()throws IOException{
        String temp;
        while((temp = reader.readLine())!=null){
            content.add(temp);
        }
        reader.close();
    }
    public TextFileReader(File target) throws IOException{
        this.target = target;
        checkFile();
        reader = new BufferedReader(new FileReader(target));
        read();
    }
    public TextFileReader(String target)throws IOException{
        this.target = new File(target);
        checkFile();
        reader = new BufferedReader(new FileReader(target));
        read();
    }

    @Override
    public Iterator<String> iterator() {
        return content.iterator();
    }
    public static void main(String[] args) {
        try{
            File file = new File("./test/test1.txt");
            FilePrinter printer = new FilePrinter(file);
            Generator<String> generator = new RandomGenerator.String(20);
            for(int i=0;i<20;++i){
                printer.println(generator.next());
            }
            printer.close();
            TextFileReader reader = new TextFileReader(file);
            for(String content:reader){
                Print.println(content);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

}
