package net.khe.db2;

import net.khe.db2.annotations.KeyNotFoundException;

import java.util.*;

/**
 * Created by hyc on 2017/3/18.
 * 表示数据表元数据
 */
public class TableMeta {
    private List<String> tables;
    private Map<String,TableField> fields = new LinkedHashMap<>();
    private TableField key;
    private Map<Class<?>,TableField> foreignMap;

    /**
     * @param tables 表名列表
     * @param fields 字段列表
     */
    public TableMeta(List<String> tables, List<TableField> fields) {
        this.tables = tables;
        for(TableField field:fields){
            this.fields.put(field.getNameInClass(),field);
        }
    }

    /**
     * 默认构造函数
     */
    public TableMeta(){
        tables = new ArrayList<>();
        fields = new HashMap<>();
    }

    /**
     * 获取表名列表
     * @return 表名列表
     */
    public List<String> getTables() {
        return tables;
    }

    /**
     * 根据字段在类中的变量名获取字段
     * @param name 字段在类中的变量名
     * @return 字段
     */
    public TableField getField(String name) {
        return fields.get(name);
    }

    /**
     * 获取字段列表
     * @return 字段列表
     */
    public List<TableField> getFields(){
        return new ArrayList<>(fields.values());
    }

    /**
     * 获取主键字段信息
     * @return 主键字段
     * @throws KeyNotFoundException 主键不存在异常
     */
    public TableField getKey() throws KeyNotFoundException {
        if(key==null)throw new KeyNotFoundException();
        return key;
    }

    /**
     * 设置主键
     * @param key 主键字段
     */
    public void setKey(TableField key) {
        this.key = key;
    }

    /**
     * 根据关联类名获取外键字段
     * @param cls 类名
     * @return 外键字段信息
     */
    public TableField getForeign(Class<?> cls) {
        return foreignMap.get(cls);
    }

    /**
     * 设置外键映射表
     * @param foreignMap 外键映射表
     */
    public void setForeignMap(Map<Class<?>, TableField> foreignMap) {
        this.foreignMap = foreignMap;
    }
}
