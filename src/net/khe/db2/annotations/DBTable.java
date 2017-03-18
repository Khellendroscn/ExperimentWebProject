package net.khe.db2.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by hyc on 2017/3/18.
 * 该注解作用于类(java bean)，表示该类的实例可被映射到数据库中
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DBTable {
    /**
     * 数据表的名称
     * 可以从多张表中获取数据，但是仅当数据表数量为1时才可以对数据表进行写入操作
     * 写入操作包括insert,update,delete
     * 表名默认为被注解类的完整类名(小写)，名称中的"."会被替换为"_"
     * @return
     */
    String[] value() default {};
}
