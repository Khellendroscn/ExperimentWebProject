package net.khe.script;

import java.util.List;

/**
 * Created by hyc on 2017/3/11.
 */

/**
 * ���ݱ�Ԫ���ݽӿ�
 */
public interface TableMeta {
    /**
     * ��ȡ����
     * @return ���ݱ�����
     */
    String getName();

    /**
     * ��ȡ�ֶ���Ϣ
     * @return �ֶ���Ϣ
     * @see Field
     */
    List<Field> getFields();
}
