package net.khe.db2.annotations;

import net.khe.db2.DataBase;
import net.khe.db2.TableField;
import net.khe.db2.TableMeta;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by hyc on 2017/3/17.
 * 注解处理器，用来取得Bean的元数据
 */
public class DBAnnotationsProcesser<T> {
    private Class<T> class_;
    private TableMeta meta;
    private final static Map<Class<? extends Annotation>,FieldCaster>
            casterMap = new HashMap<>();
    static {
        casterMap.put(SqlByte.class,DBAnnotationsProcesser::castByte);
        casterMap.put(SqlShort.class,DBAnnotationsProcesser::castShort);
        casterMap.put(SqlInt.class,DBAnnotationsProcesser::castInt);
        casterMap.put(SqlLong.class,DBAnnotationsProcesser::castLong);
        casterMap.put(SqlChars.class,DBAnnotationsProcesser::castChars);
        casterMap.put(SqlString.class,DBAnnotationsProcesser::castString);
        casterMap.put(SqlFloat.class,DBAnnotationsProcesser::castFloat);
        casterMap.put(SqlDouble.class,DBAnnotationsProcesser::castDouble);
    }

    /**
     * 构造函数
     * @param class_ 需要处理的类
     * @throws KeyNotFoundException 主键不存在异常
     * @throws ClassNotFoundException 类不存在异常
     */
    public DBAnnotationsProcesser(Class<T> class_) throws KeyNotFoundException, ClassNotFoundException {
        this.class_ = class_;
        initFields();
    }
    private void initFields() throws KeyNotFoundException, ClassNotFoundException {
        DBTable tableAnno = class_.getAnnotation(DBTable.class);
        List<String> tbNames =
                tableAnno.value().length>0?
                        Arrays.asList(tableAnno.value()):
                        Arrays.asList(class_.getName().replace('.','_'));
        List<TableField> fields = new ArrayList<>();
        Field[] objFields = class_.getDeclaredFields();
        TableField key = null;
        Map<Class<?>,TableField> foreignMap = new HashMap<>();
        for(Field field:objFields){
            TableField tf = null;
            for(Class<? extends Annotation> cls:casterMap.keySet()){
                Annotation anno = field.getAnnotation(cls);
                if(anno!=null){
                    tf = casterMap.get(cls).toField(anno);
                    tf.setNameInClass(field.getName());
                    break;
                }
            }
            if(tf!=null){
                Constraints constraints =
                        field.getAnnotation(Constraints.class);
                if(constraints!=null){
                    Map<String,Boolean> map = tf.getConstraints();
                    map.put("primaryKey",constraints.primaryKey());
                    map.put("alloNull",constraints.alloNull());
                    map.put("unique",constraints.unique());
                    map.put("autoIncrement",constraints.autoIncrement());
                    if(map.get("primaryKey")){
                        key = tf;
                    }
                }
                Foreign foreignAnno =
                        field.getAnnotation(Foreign.class);
                if(foreignAnno!=null){
                    Class c = foreignAnno.value();
                    foreignMap.put(c,tf);
                }
            }
            if(tf!=null) fields.add(tf);
        }
        if(tbNames.size()==1)
            addTbName(tbNames.get(0),fields);
        meta = new TableMeta(tbNames,fields);
        meta.setKey(key);
        meta.setForeignMap(foreignMap);
    }
    private static void addTbName(String tbName,List<TableField> fields){
        for(TableField field:fields){
            String name = field.getName();
            if(!name.contains(".")){
                field.setName(String.format("%s.%s",tbName,field.getName()));
            }
        }
    }
    private static TableField castByte(Annotation anno){
        SqlByte byteAnno = (SqlByte)anno;
        return new TableField(byteAnno.value(),"TINYINT");
    }
    private static TableField castShort(Annotation anno){
        SqlShort shotAnno = (SqlShort)anno;
        return new TableField(shotAnno.value(),"SMALLINT");
    }
    private static TableField castInt(Annotation anno){
        SqlInt intAnno = (SqlInt)anno;
        return new TableField(intAnno.value(),"INT");
    }
    private static TableField castLong(Annotation anno){
        SqlLong longAnno = (SqlLong)anno;
        return new TableField(longAnno.value(),"INTEGER");
    }
    private static TableField castChars(Annotation anno){
        SqlChars charsAnno = (SqlChars) anno;
        int len = charsAnno.value();
        return new TableField(charsAnno.name(),String.format("CHAR(%d)",len));
    }
    private static TableField castString(Annotation anno){
        SqlString strAnno = (SqlString) anno;
        int len = strAnno.value();
        if(len>0)
            return new TableField(strAnno.name(),String.format("VARCHAR(%d)",len));
        else
            return new TableField(strAnno.name(),"VARCHAR(255)");
    }
    private static TableField castFloat(Annotation anno){
        SqlFloat floatAnno = (SqlFloat)anno;
        return new TableField(floatAnno.value(),"NUMERIC");
    }
    private static TableField castDouble(Annotation anno){
        SqlDouble doubleAnno = (SqlDouble)anno;
        return new TableField(doubleAnno.value(),"DECIMAL");
    }

    /**
     * 获取数据表的元数据
     * @return 元数据
     * @see TableMeta
     */
    public TableMeta getMeta() {
        return meta;
    }
}
interface FieldCaster{
    TableField toField(Annotation annotation);
}