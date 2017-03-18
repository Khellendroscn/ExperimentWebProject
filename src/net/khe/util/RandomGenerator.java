package net.khe.util;

import java.awt.*;
import java.util.Random;

/**
 * Created by hyc on 2016/10/14.
 */
public class RandomGenerator {
    private static Random rand = new Random();
    public static class Boolean implements Generator<java.lang.Boolean>{
        public java.lang.Boolean next(){
            return rand.nextBoolean();
        }
    }
    public static class Byte implements Generator<java.lang.Byte>{
        public java.lang.Byte next(){
            return (byte) rand.nextInt();
        }
    }
    public static class Character implements Generator<java.lang.Character>{
        private char[] charSet = CountingGenerator.charArr;
        public Character(){}
        public Character(java.lang.String charSet){
            this.charSet = charSet.toCharArray();
        }
        public java.lang.Character next(){
            return charSet[rand.nextInt(charSet.length)];
        }
    }
    public static class String extends CountingGenerator.String{
        {
            super.cg = new Character();
        }
        public String(){}
        public String (int length){
            super(length);
        }
    }
    public static class Short implements Generator<java.lang.Short>{
        public java.lang.Short next(){
            return (short)rand.nextInt();
        }
    }
    public static class Integer implements Generator<java.lang.Integer>{
        private int mod = 10000;
        public Integer(){}
        public Integer(int modulo){
            mod = modulo;
        }
        public java.lang.Integer next(){
            return rand.nextInt(mod);
        }
    }
    public static class Long implements Generator<java.lang.Long>{
        private int mod = 10000;
        public Long(){}
        public Long(int modulo){
            mod = modulo;
        }
        public java.lang.Long next(){
            return (long)rand.nextInt(mod);
        }
    }
    public static class Float implements Generator<java.lang.Float>{
        public java.lang.Float next(){
            int temp = Math.round(rand.nextFloat()*100);
            return ((float) temp)/100;
        }
    }
    public static class Double implements Generator<java.lang.Double>{
        public java.lang.Double next(){
            long temp = Math.round(rand.nextDouble()*100);
            return ((double) temp)/100;
        }
    }
    public static class PureColor implements Generator<java.awt.Color>{
        private Generator<java.lang.Integer> gen = new Integer(5);
        @Override
        public java.awt.Color next(){
            switch (gen.next()){
                case 0:return java.awt.Color.WHITE;
                case 1:return java.awt.Color.RED;
                case 2:return java.awt.Color.YELLOW;
                case 3:return java.awt.Color.GREEN;
                case 4:return java.awt.Color.BLUE;
            }
            return null;
        }
    }
    public static class Color implements Generator<java.awt.Color>{
        private Generator<java.lang.Integer> gen = new Integer(0xff);
        @Override
        public java.awt.Color next() {
            return new java.awt.Color(gen.next(),gen.next(),gen.next());
        }
    }
    public static void main(java.lang.String[] args){
        GeneratorsTest.test(RandomGenerator.class);
    }
}
