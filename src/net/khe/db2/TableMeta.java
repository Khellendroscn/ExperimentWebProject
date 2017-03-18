package net.khe.db2;

import net.khe.db2.annotations.KeyNotFoundException;

import java.util.*;

/**
 * Created by hyc on 2017/3/18.
 */
public class TableMeta {
    private List<String> tables;
    private Map<String,TableField> fields = new LinkedHashMap<>();
    private TableField key;
    private Map<Class<?>,TableField> foreignMap;

    public TableMeta(List<String> tables, List<TableField> fields) {
        this.tables = tables;
        for(TableField field:fields){
            this.fields.put(field.getNameInClass(),field);
        }
    }
    public TableMeta(){
        tables = new ArrayList<>();
        fields = new HashMap<>();
    }

    public List<String> getTables() {
        return tables;
    }

    public TableField getField(String name) {
        return fields.get(name);
    }
    public List<TableField> getFields(){
        return new ArrayList<>(fields.values());
    }
    public TableField getKey() throws KeyNotFoundException {
        if(key==null)throw new KeyNotFoundException();
        return key;
    }

    public void setKey(TableField key) {
        this.key = key;
    }

    public TableField getForeigns(Class<?> cls) {
        return foreignMap.get(cls);
    }

    public void setForeignMap(Map<Class<?>, TableField> foreignMap) {
        this.foreignMap = foreignMap;
    }
}
