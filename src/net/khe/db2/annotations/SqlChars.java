package net.khe.db2.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by hyc on 2017/3/17.
 * 表示数据库中的定长字符串CHAR
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SqlChars {
    /**
     * 字符串长度
     * @return 字符串长度
     */
    int value();
    /**
     * 在数据表中的字段名
     * 如果Bean需要从多张表获取数据，请填写完整字段名（即【表名.字段名】）
     * 默认与Bean中的变量名相同
     * @return 字段名
     */
    String name() default "";
}
