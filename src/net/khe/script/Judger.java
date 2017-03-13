package net.khe.script;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by hyc on 2017/3/9.
 */
/**
 * �ж����ӿڣ��ж�һ��ʵ�������Ƿ����Ҫ��
 * Judger�������ͨ��Parser����
 * Judger����֧�����л�
 * @version alpha
 * @author Khellendros
 * @see Parser
 */
public interface Judger extends Serializable{
    /**
     * ͨ���÷�������Judger
     * @param row ������
     * @throws Variable.TypeException
     */
    void judge(Map<String,Variable> row) throws Variable.TypeException;

    /**
     * @return ������
     */
    Map<String,Variable> getVars();
    /**
     * ��ս����
     */
    void clearResultSet();

    /**
     * @return �ύ���
     */
    Double getSubmit();
}
