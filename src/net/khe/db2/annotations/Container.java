package net.khe.db2.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by hyc on 2017/3/26.
 * 该注解用于标记Bean中的集合字段（即java.util.Collection及其派生）。
 * 由于java的泛型擦除特性，无法从字段中抽取出集合中元素的类型，
 * 因此元素的类型也必须在注解中注明
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Container {
    /**
     * 集合元素的类型名称（带包名的全称）
     * @return
     */
    String elementType();

    /**
     * 集合类型名称（带报名的全称）。
     * ！注意，该类型不能是接口或抽象类
     * @return
     */
    String containerType();
}
