package net.khe.db2;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hyc on 2017/3/17.
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

    public TableField(String name, String type) {
        this.name = name;
        this.type = type;
    }
    public Map<String,Boolean> getConstraints(){return constraints;}

    public String getName() {
        if(name!=null&&!name.equals(""))
            return name;
        else
            return nameInClass;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public void setNameInClass(String name){
        this.nameInClass = name;
    }
    public String getNameInClass(){return nameInClass;}
    @Override
    public String toString(){
        return String.format("TableField(name=%s, type=%s, constrains=%s)",getName(),getType(),constraints);
    }
    public String toSql(){
        String sql = getName()+" "+getType();
        if(!getConstraints().get("alloNull")){
            sql+=" NOT NULL";
        }
        if(getConstraints().get("primaryKey")){
            sql+=" PRIMARY KEY";
        }
        if(getConstraints().get("unique")){
            sql+=" UNIQUE";
        }
        return sql;
    }
}
