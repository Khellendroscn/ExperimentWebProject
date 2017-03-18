package net.khe.util;

import java.util.Arrays;

/**
 * Created by hyc on 2016/10/14.
 */
public class Range {
    public static int[] range(int begin,int end,int step) {
        if(step <= 0) return null;
        int size = (int) ((double)(end-begin)/step+0.5);
        int[] result = new int[size];
        for(int i = 0,j=begin;j<end;i++,j+=step)
        {
            result[i] = j;
        }
        return result;
    }
    public static int[] range(int size) {
        return range(0,size,1);
    }
    public static int[] range(int begin,int size) {
        return range(begin,size,1);
    }
    public static void main(String[] args){
        Print.println(Arrays.toString(range(0,11,3)));
    }
}
