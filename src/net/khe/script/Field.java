package net.khe.script;

/**
 * Created by hyc on 2017/3/11.
 */

/**
 * �������ݱ��ֶ���Ϣ����
 */
public class Field {
    public final String name;
    public final Type type;

    /**
     * ���캯��
     * @param name �ֶ���
     * @param type �ֶ�����
     */
    public Field(String name, Type type) {
        this.name = name;
        this.type = type;
    }
}
