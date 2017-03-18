package net.khe.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by hyc on 2017/3/18.
 */
public class ClassVisitor<T> {
    private Class<T> cls;
    public ClassVisitor(Class<T> cls){
        this.cls = cls;
    }
    public Method getGetter(Field field) throws NoSuchMethodException {
        StringBuilder sb = new StringBuilder("get"+field.getName());
        sb.setCharAt(3,Character.toUpperCase(sb.charAt(3)));
        return cls.getMethod(sb.toString());
    }
    public Method getSetter(Field field) throws NoSuchMethodException {
        StringBuilder sb = new StringBuilder("set"+field.getName());
        sb.setCharAt(3,Character.toUpperCase(sb.charAt(3)));
        return cls.getMethod(sb.toString(),field.getType());
    }
}
