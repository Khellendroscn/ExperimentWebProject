package net.khe.db2.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by hyc on 2017/3/17.
 * 表示Sql中的大实数DECIMAL，
 * 注意：请对java.lang.BigDecimal类型的字段使用此注解，而不是double或float
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SqlDecimal {
    /**
     * 在数据表中的字段名
     * 如果Bean需要从多张表获取数据，请填写完整字段名（即【表名.字段名】）
     * 默认与Bean中的变量名相同
     * @return 字段名
     */
    String value() default "";

    /**
     * 整数部分长度
     * @return 整数部分长度
     */
    int size()default -1;

    /**
     * 小数位数
     * @return 小数位数
     */
    int d()default -1;
}
