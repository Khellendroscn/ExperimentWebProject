package net.khe.util;

/**
 * Created by hyc on 2016/10/14.
 */
public class CountingGenerator{
    public static class Boolean implements Generator<java.lang.Boolean>{
        private boolean value = false;
        public java.lang.Boolean next() {
            value = !value;
            return value;
        }
    }
    public static class Byte implements Generator<java.lang.Byte>{
        private byte value = 0;
        public java.lang.Byte next(){
            return value++;
        }
    }
    static char[] charArr = ("abcdefghijklmnopqrstuvwxyz"+
            "ABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();
    public static class Character implements Generator<java.lang.Character>{
        int index = -1;
        public java.lang.Character next(){
            index = (index+1)%charArr.length;
            return charArr[index];
        }
    }
    public static class String implements Generator<java.lang.String>{
        private int length = 7;
        Generator<java.lang.Character> cg = new Character();
        public String(){}
        public String(int length){
            this.length = length;
        }
        public java.lang.String next(){
            char[] buffer = new char[length];
            for(int i = 0;i<length;++i){
                buffer[i] = cg.next();
            }
            return new java.lang.String(buffer);
        }
    }
    public static class Short implements Generator<java.lang.Short>{
        private short value = 0;
        public java.lang.Short next(){
            return value++;
        }
    }
    public static class Integer implements Generator<java.lang.Integer>{
        private int value = 0;
        public java.lang.Integer next(){
            return value++;
        }
    }
    public static class Float implements Generator<java.lang.Float>{
        private float value = 0;
        public java.lang.Float next(){
            return value+=1.0;
        }
    }
    public static class Double implements Generator<java.lang.Double>{
        private double value = 0.0;
        public java.lang.Double next(){
            return value+=1.0;
        }
    }
    public static void main(java.lang.String[] args){
        GeneratorsTest.test(CountingGenerator.class);
    }
}
class GeneratorsTest{
    private static int size = 10;
    public static void test(Class<?> surroundingClass){
        for(Class<?> type:surroundingClass.getClasses()){
            Print.print(type.getSimpleName()+" : ");
            try{
                Generator<?> g = (Generator<?>)type.newInstance();
                for(int i=0;i<size;++i){
                    Print.print(g.next()+" ");
                }
                Print.println();
            }catch (Exception e){
                throw new RuntimeException(e);
            }
        }
    }
}