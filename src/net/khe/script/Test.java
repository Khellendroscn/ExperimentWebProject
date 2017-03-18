package net.khe.script;

import java.io.*;
import java.util.*;

/**
 * Created by hyc on 2017/3/11.
 */

/**
 * 测试解析器
 */
public class Test {
    private final static TableMeta meta =
            new TableMeta("test",
                    Arrays.asList(
                            new Field("a",Type.ARRAY),
                            new Field("b",Type.ARRAY),
                            new Field("c",Type.NUM)
                    ));
    private final static Map<String,Variable> table = new HashMap<>();
    static {
        table.put("a",new Array(Arrays.asList(
                new Num(1.0),
                new Num(2.0),
                new Num(3.0)
        )));
        table.put("b",new Array(Arrays.asList(
                new Num(1.0),
                new Num(2.0),
                new Num(3.0)
        )));
        table.put("c",new Num(2.0));
    }
    private final static String text = "$var = 3.0;\n"+
            "$var+$a[0]+$b[1]==sum($b)+$c*2-4;\n"+
            "$avg_a = avg($a);\n"+
            "$avg_a == sqrt(4);\n"+
            "$arr = {-1};\n"+
            "$arr[0] = 9.99\n"+
            "$var = 10;\n"+
            "$arr = $arr+$a+$var;\n"+
            "$r=results();\n"+
            "sum($arr) == 15;\n"+
            "$r[2]=0;\n"+
            "submit sum($r);";

    public static void main(String[] args) {
        System.out.println("脚本：");
        System.out.println(text);
        System.out.println("数据：");
        System.out.println(table);
        System.out.println("结果：");
        try {
            Parser parser = new ScriptParser(text,meta);
            parser.parse();
            Judger judger = parser.getJudger();
            judger.judge(table);
            System.out.println(judger.getSubmit());
            System.out.println("================测试序列化==============");
            judger.clearResultSet();
            testSerialize(judger);
        } catch (Parser.ParseException e) {
            e.printStackTrace();
        } catch (Variable.TypeException e) {
            e.printStackTrace();
        }
    }
    public static void testSerialize(Judger judger){
        try {
            ObjectOutputStream oos =
                    new ObjectOutputStream(
                            new FileOutputStream("test/test.obj")
                    );
            oos.writeObject(judger);
            oos.close();
            ObjectInputStream ois =
                    new ObjectInputStream(
                            new FileInputStream("test/test.obj")
                    );
            Judger j = (Judger)ois.readObject();
            j.judge(table);
            System.out.println(j.getSubmit());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Variable.TypeException e) {
            e.printStackTrace();
        }
    }
}
