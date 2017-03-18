package net.khe.util;

import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.Arrays;

/**
 * Created by hyc on 2016/10/14.
 */
public class PackArray {
    //基本类型数组->包装器数组
    public static Boolean[] packUp(boolean[] array){
        Boolean[] packArr = new Boolean[array.length];
        for(int i=0;i<array.length;++i){
            packArr[i] = array[i];
        }
        return packArr;
    }
    public static Character[] packUp(char[] array){
        Character[] packArr = new Character[array.length];
        for(int i=0;i<array.length;++i){
            packArr[i] = array[i];
        }
        return packArr;
    }
    public static Byte[] packUp(byte[] array){
        Byte[] packArr = new Byte[array.length];
        for(int i=0;i<array.length;++i){
            packArr[i] = array[i];
        }
        return packArr;
    }
    public static Short[] packUp(short[] array){
        Short[] packArr = new Short[array.length];
        for(int i=0;i<array.length;++i){
            packArr[i] = array[i];
        }
        return packArr;
    }
    public static Integer[] packUp(int[] array){
        Integer[] packArr = new Integer[array.length];
        for(int i=0;i<array.length;++i){
            packArr[i] = array[i];
        }
        return packArr;
    }
    public static Long[] packUp(long[] array){
        Long[] packArr = new Long[array.length];
        for(int i=0;i<array.length;++i){
            packArr[i] = array[i];
        }
        return packArr;
    }
    public static Float[] packUp(float[] array){
        Float[] packArr = new Float[array.length];
        for(int i=0;i<array.length;++i){
            packArr[i] = array[i];
        }
        return packArr;
    }
    public static Double[] packUp(double[] array){
        Double[] packArr = new Double[array.length];
        for(int i=0;i<array.length;++i){
            packArr[i] = array[i];
        }
        return packArr;
    }
    public static void main(String[] args){
        int[] intArr = {1,2,3,4,5};
        Integer[] integers = packUp(intArr);
        Print.println(Arrays.toString(integers));
    }
}
