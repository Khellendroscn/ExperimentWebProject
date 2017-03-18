package net.khe.script;

import java.util.List;

/**
 * Created by hyc on 2017/3/11.
 */

/**
 * 数据表元数据接口
 */
public class TableMeta {
    private String name;
    private List<Field> fields;

    public TableMeta(String name, List<Field> fields) {
        this.name = name;
        this.fields = fields;
    }

    /**
     * 获取表名
     * @return 数据表名称
     */
    public String getName(){return name;}

    /**
     * 获取字段信息
     * @return 字段信息
     * @see Field
     */
    public List<Field> getFields(){return fields;}
}
