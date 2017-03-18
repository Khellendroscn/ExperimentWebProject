package net.khe.util;

import java.util.Arrays;

/**
 * Created by hyc on 2016/10/14.
 */
public class ArrayFiller<T> {
    public static <T> T[] filledArray(Class<T> type,Generator<T> generator,int size){
        T[] arr = (T[]) java.lang.reflect.Array.newInstance(type,size);
        return new CollectionData<T>(generator,size).toArray(arr);
    }
    public static <T> T[] fill(T[] arr,Generator<T> generator){
        return new CollectionData<T>(generator,arr.length).toArray(arr);
    }
    public static void main(String[] args){
        Integer[] arr1 = ArrayFiller.filledArray(
                Integer.class,new CountingGenerator.Integer(),10
        );
        String[] arr2 = new String[10];
        arr2 = ArrayFiller.fill(arr2,new RandomGenerator.String());
        Print.println(Arrays.toString(arr1));
        Print.println(Arrays.toString(arr2));
    }
}
