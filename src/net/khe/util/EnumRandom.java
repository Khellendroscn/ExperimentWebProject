package net.khe.util;

import java.util.Random;

/**
 * Created by hyc on 2016/10/19.
 */
public class EnumRandom{
    private static Random rand = new Random();
    public static <T extends Enum<T>> T random(Class<T> ec){
        return random(ec.getEnumConstants());
    }
    public static <T> T random(T[] values){
        return values[rand.nextInt(values.length)];
    }
}
