package net.khe.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
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
    public static Object getAttr(Object obj, String fieldName) throws NoSuchFieldException, InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Class cls = obj.getClass();
        ClassVisitor visitor = new ClassVisitor(cls);
        Method getter = visitor.getGetter(cls.getDeclaredField(fieldName));
        return getter.invoke(obj);
    }
    public static void setAttr(Object obj, String fieldName, Object value) throws NoSuchFieldException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class cls = obj.getClass();
        ClassVisitor visitor = new ClassVisitor(cls);
        Method setter = visitor.getSetter(cls.getDeclaredField(fieldName));
        setter.invoke(obj,value);
    }
}
