package net.khe.db2.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by hyc on 2017/3/26.
 * ��ע�����ڱ��Bean�еļ����ֶΣ���java.util.Collection������������
 * ����java�ķ��Ͳ������ԣ��޷����ֶ��г�ȡ��������Ԫ�ص����ͣ�
 * ���Ԫ�ص�����Ҳ������ע����ע��
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Container {
    /**
     * ����Ԫ�ص��������ƣ���������ȫ�ƣ�
     * @return
     */
    String elementType();

    /**
     * �����������ƣ���������ȫ�ƣ���
     * ��ע�⣬�����Ͳ����ǽӿڻ������
     * @return
     */
    String containerType();
}
