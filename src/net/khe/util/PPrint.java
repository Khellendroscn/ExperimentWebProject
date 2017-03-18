package net.khe.util;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by hyc on 2016/10/16.
 */
public class PPrint {
    public static String pformat(Collection<?> c){
        if(c.size() == 0) return "[]";
        StringBuilder builder = new StringBuilder("[");
        for(Object elem : c){
            if(c.size()!=1) builder.append("\n  ");
            builder.append(elem);
        }
        if(c.size()!=1) builder.append("\n");
        builder.append("]");
        return builder.toString();
    }
    public static void pprintln(Collection<?> c){
        Print.println(pformat(c));
    }
    public static void pprintln(Object[] c){
        Print.println(pformat(Arrays.asList(c)));
    }

    public static void main(String[] args) {
        pprintln(Arrays.asList(1,2,3,4,5,6));
    }
}
