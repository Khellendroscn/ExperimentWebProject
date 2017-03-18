package net.khe.util;

import java.util.Arrays;

/**
 * Created by hyc on 2016/10/14.
 */
public class UnpackArray {
    //包装类数组->基本类型数组
    public static boolean[] unpack(Boolean[] array){
        boolean[] unpackArr = new boolean[array.length];
        for(int i=0;i<array.length;++i){
            unpackArr[i] = array[i];
        }
        return unpackArr;
    }
    public static byte[] unpack(Byte[] array){
        byte[] unpackArr = new byte[array.length];
        for(int i=0;i<array.length;++i){
            unpackArr[i] = array[i];
        }
        return unpackArr;
    }
    public static char[] unpack(Character[] array){
        char[] unpackArr = new char[array.length];
        for(int i=0;i<array.length;++i){
            unpackArr[i] = array[i];
        }
        return unpackArr;
    }
    public static short[] unpack(Short[] array){
        short[] unpackArr = new short[array.length];
        for(int i=0;i<array.length;++i){
            unpackArr[i] = array[i];
        }
        return unpackArr;
    }
    public static int[] unpack(Integer[] array){
        int[] unpackArr = new int[array.length];
        for(int i=0;i<array.length;++i){
            unpackArr[i] = array[i];
        }
        return unpackArr;
    }
    public static long[] unpack(Long[] array){
        long[] unpackArr = new long[array.length];
        for(int i=0;i<array.length;++i){
            unpackArr[i] = array[i];
        }
        return unpackArr;
    }
    public static float[] unpack(Float[] array){
        float[] unpackArr = new float[array.length];
        for(int i=0;i<array.length;++i){
            unpackArr[i] = array[i];
        }
        return unpackArr;
    }
    public static double[] unpack(Double[] array){
        double[] unpackArr = new double[array.length];
        for(int i=0;i<array.length;++i){
            unpackArr[i] = array[i];
        }
        return unpackArr;
    }
    public static void main(String[] args){
        int[] ints = unpack(ArrayFiller.filledArray
                (Integer.class,new RandomGenerator.Integer(),10));
        Print.println(Arrays.toString(ints));
    }
}
