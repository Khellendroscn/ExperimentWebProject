package net.khe.db2;

import net.khe.util.Generator;

import java.util.stream.Stream;

/**
 * Created by hyc on 2017/3/26.
 * DBQuary�ӿڣ��̳��������ӿڣ�������ʹ��stream����������
 * @param <T> ���ɵ�Ԫ������
 */
public interface DBQuery<T> extends Generator<T> {
    /**
     * �÷�����������������ʹ��java 8 stream�ӿڶ����ݽ��йܵ�ʽ����
     * @return Stream����
     */
    Stream<T> stream();
}
