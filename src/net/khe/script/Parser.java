package net.khe.script;
/**
 * Created by hyc on 2017/3/9.
 */

import java.io.Serializable;

/**
 * �ݹ��½������������ڽ�����ʽ
 * @version alpha
 * @author Khellendros
 */
public interface Parser extends Serializable{
    /**
     * ͨ���÷�������������
     * @throws ParseException �ű������쳣
     */
    void parse() throws ParseException;

    /**
     * ��ȡjudger
     * @return ����һ���ж���
     * @see Judger
     */
    Judger getJudger();

    /**
     * �����쳣
     */
    public static class ParseException extends Exception{
        /**
         * @param what �쳣˵��
         * @param line �����쳣�Ľű�����
         */
        public ParseException(String what,int line){
                super(what+" at line "+line);
        }
    }
}
