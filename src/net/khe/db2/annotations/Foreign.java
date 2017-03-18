package net.khe.db2.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by hyc on 2017/3/18.
 * 该注解表示外键，作用于字段
 * 外键仅表示Bean之间的映射关系，并非数据库中的物理外键
 * 因此对Bean添加外键不会对数据表产生任何影响
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Foreign {
    /**
     * 将外键绑定到一个Bean上，注解的参数为类的全名
     * 绑定完成之后，被外键注解的字段会映射到被绑定的Bean的主键字段
     * @return 被绑定的类的全名
     */
    String value();
}
