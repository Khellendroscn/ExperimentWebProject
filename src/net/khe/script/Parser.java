package net.khe.script;
/**
 * Created by hyc on 2017/3/9.
 */

import java.io.Serializable;

/**
 * 递归下降解析器，用于解析公式
 * @version alpha
 * @author Khellendros
 */
public interface Parser extends Serializable{
    /**
     * 通过该方法启动解析器
     * @throws ParseException 脚本解析异常
     */
    void parse() throws ParseException;

    /**
     * 获取judger
     * @return 返回一个判断器
     * @see Judger
     */
    Judger getJudger();

    /**
     * 解析异常
     */
    public static class ParseException extends Exception{
        /**
         * @param what 异常说明
         * @param line 出现异常的脚本行数
         */
        public ParseException(String what,int line){
                super(what+" at line "+line);
        }
    }
}
