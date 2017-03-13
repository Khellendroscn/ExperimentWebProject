package net.khe.script;

/**
 * Created by hyc on 2017/3/11.
 */

/**
 * 保存数据表字段信息的类
 */
public class Field {
    public final String name;
    public final Type type;

    /**
     * 构造函数
     * @param name 字段名
     * @param type 字段类型
     */
    public Field(String name, Type type) {
        this.name = name;
        this.type = type;
    }
}
