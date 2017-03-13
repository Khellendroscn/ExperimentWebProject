package net.khe.script;

import java.util.List;

/**
 * Created by hyc on 2017/3/11.
 */

/**
 * 数据表元数据接口
 */
public interface TableMeta {
    /**
     * 获取表名
     * @return 数据表名称
     */
    String getName();

    /**
     * 获取字段信息
     * @return 字段信息
     * @see Field
     */
    List<Field> getFields();
}
