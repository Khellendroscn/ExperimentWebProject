package net.khe.script;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Created by hyc on 2017/3/9.
 */
/**
 * 判断器接口，判断一行实验数据是否符合要求
 * Judger对象可以通过Parser产生
 * Judger对象支持序列化
 * @version alpha
 * @author Khellendros
 * @see Parser
 */
public interface Judger extends Serializable{
    /**
     * 通过该方法启动Judger
     * @param row 数据行
     * @throws Variable.TypeException
     */
    void judge(Map<String,Variable> row) throws Variable.TypeException;

    /**
     * @return 变量集
     */
    Map<String,Variable> getVars();
    /**
     * 清空结果集
     */
    void clearResultSet();

    /**
     * @return 提交结果
     */
    Double getSubmit();
}
