package net.khe.script;

import java.util.List;

/**
 * Created by hyc on 2017/3/11.
 */

/**
 * ���ݱ�Ԫ���ݽӿ�
 */
public class TableMeta {
    private String name;
    private List<Field> fields;

    public TableMeta(String name, List<Field> fields) {
        this.name = name;
        this.fields = fields;
    }

    /**
     * ��ȡ����
     * @return ���ݱ�����
     */
    public String getName(){return name;}

    /**
     * ��ȡ�ֶ���Ϣ
     * @return �ֶ���Ϣ
     * @see Field
     */
    public List<Field> getFields(){return fields;}
}
