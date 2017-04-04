package net.khe.db2.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by hyc on 2017/3/17.
 * ��ʾSql�еĴ�ʵ��DECIMAL��
 * ע�⣺���java.lang.BigDecimal���͵��ֶ�ʹ�ô�ע�⣬������double��float
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SqlDecimal {
    /**
     * �����ݱ��е��ֶ���
     * ���Bean��Ҫ�Ӷ��ű��ȡ���ݣ�����д�����ֶ�������������.�ֶ�������
     * Ĭ����Bean�еı�������ͬ
     * @return �ֶ���
     */
    String value() default "";

    /**
     * �������ֳ���
     * @return �������ֳ���
     */
    int size()default -1;

    /**
     * С��λ��
     * @return С��λ��
     */
    int d()default -1;
}
