package net.khe.db2;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hyc on 2017/3/17.
 * 字段元数据类
 */
public class TableField {
    private String name;
    private String type;
    private String nameInClass;
    private Map<String,Boolean> constraints = new HashMap<>();
    {
        constraints.put("primaryKey",false);
        constraints.put("alloNull",true);
        constraints.put("unique",false);
    }

    /**
     * @param name 字段名
     * @param type 字段类型
     */
    public TableField(String name, String type) {
        this.name = name;
        this.type = type;
    }

    /**
     * 获取约束
     * @return 约束
     */
    public Map<String,Boolean> getConstraints(){return constraints;}

    /**
     * 获取字段名
     * @return 字段名
     */
    public String getName() {
        if(name!=null&&!name.equals(""))
            return name;
        else
            return nameInClass;
    }

    /**
     * 设置字段名
     * @param name 字段名
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取字段类型
     * @return 字段类型
     */
    public String getType() {
        return type;
    }

    /**
     * 设置字段类型
     * @param type 字段类型
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 设置字段在类中的变量名
     * @param name 字段在类中的变量名
     */
    public void setNameInClass(String name){
        this.nameInClass = name;
    }

    /**
     * 获取字段在类中的变量名
     * @return 字段在类中的变量名
     */
    public String getNameInClass(){return nameInClass;}
    @Override
    public String toString(){
        return String.format("TableField(name=%s, type=%s, constrains=%s)",getName(),getType(),constraints);
    }

    /**
     * 转换成在sql中的表示（用于建表）
     * @return 字段名 类型 约束...
     */
    public String toSql(){
        String sql = getName()+" "+getType();
        if(!getConstraints().get("alloNull")){
            sql+=" NOT NULL";
        }
        if(getConstraints().get("primaryKey")){
            sql+=" PRIMARY KEY";
            if(getConstraints().get("autoIncrement")){
                sql+=" AUTO_INCREMENT";
            }
        } else if(getConstraints().get("unique")){
            sql+=" UNIQUE";
        }
        return sql;
    }
}
