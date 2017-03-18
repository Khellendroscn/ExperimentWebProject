package net.khe.util;

/**
 * Created by hyc on 2016/10/7.
 */
public class Print {
    public static void println(Object obj){
        System.out.println(obj);
    }
    public static void print(Object obj){
        System.out.print(obj);
    }
    /*public static void println(Object... args){
        System.out.println(args);
    }
    public static void print(Object... args){
        System.out.print(args);
    }*/
    public static void println(){System.out.println();}
    public static void printf(String format,Object...args){
        System.out.printf(format,args);
    }
    public static void main(String[] args){
        println("HelloWorld");
        print("Hello");
        println();
    }
}
