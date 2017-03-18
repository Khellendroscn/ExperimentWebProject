package net.khe.db2.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by hyc on 2017/3/17.
 * 该注解表示sql约束，作用于字段
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Constraints {
    /**
     * 主键约束
     * @return true-该字段为主键
     */
    boolean primaryKey() default  false;

    /**
     * 空值约束
     * @return false-该字段不允许为null
     */
    boolean alloNull() default true;

    /**
     * unique约束
     * @return true-该字段不可重复
     */
    boolean unique() default false;
}
