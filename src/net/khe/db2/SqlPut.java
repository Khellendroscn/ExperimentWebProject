package net.khe.db2;

import net.khe.db2.annotations.Container;
import net.khe.db2.annotations.DBTable;
import net.khe.db2.annotations.KeyNotFoundException;
import net.khe.util.ClassVisitor;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

/**
 * Created by hyc on 2017/3/21.
 */
public class SqlPut<T> implements SqlWriteOperator{
    private DataBase<T> db;
    private Class<T> cls;
    private T obj;
    private static Map<Class<?>,String> insertMap = new HashMap<>();
    private static Map<Class<?>,String> updateMap = new HashMap<>();
    SqlPut(DataBase<T> db, Class<T> cls){
        this.db = db;
        this.cls = cls;
    }
    public void setObject(T obj){
        this.obj = obj;
    }
    public void execute(T obj) throws DBWriteException {
        setObject(obj);
        execute();
    }
    public void execute() throws DBWriteException {
        try {
            T temp = db.getInstance(getKey());
            if (temp == null) {
                insert();
            } else {
                update();
            }
        }catch (DBWriteException e1){
            throw e1;
        }catch (Exception e2){
            throw new DBWriteException(e2);
        }
    }
    private Object getKey() throws
            KeyNotFoundException,
            ClassNotFoundException,
            NoSuchFieldException,
            NoSuchMethodException,
            InvocationTargetException,
            IllegalAccessException {
        TableField key = db.lookUp(cls).getKey();
        ClassVisitor visitor = new ClassVisitor(cls);
        Method getter = visitor.getGetter(cls.getDeclaredField(key.getNameInClass()));
        Class c = obj.getClass();
        return getter.invoke(obj);
    }
    private void insert() throws DBWriteException {
        try {
            if (!insertMap.containsKey(cls))
                insertMap.put(cls, prepareInsertSql());
            String sql = insertMap.get(cls);
            PreparedStatement stmt = db.getConn().prepareStatement(sql);
            setStatement(stmt);
            stmt.executeUpdate();
        }catch (DBWriteException e1){
            throw e1;
        }catch (Exception e2){
            throw new DBWriteException(e2);
        }
    }
    private void update() throws DBWriteException {
        try {
            if (!updateMap.containsKey(cls))
                updateMap.put(cls, prepareUpdateSql());
            String sql = updateMap.get(cls);
            PreparedStatement stmt = db.getConn().prepareStatement(sql);
            int i = setStatement(stmt);
            stmt.setObject(i, getKey());
            stmt.executeUpdate();
        }catch (DBWriteException e1){
            throw e1;
        }catch (Exception e2){
            throw new DBWriteException(e2);
        }
    }
    private int setStatement(PreparedStatement stmt) throws DBWriteException{
        //TODO:重构这坨方法
        try {
            TableMeta meta = db.lookUp(cls);
            int i = 1;
            for (Field field : cls.getDeclaredFields()) {
                ClassVisitor visitor = new ClassVisitor(cls);
                Method getter = visitor.getGetter(field);
                Object prop = getter.invoke(obj);
                TableField tf = meta.getField(field.getName());
                if (tf != null) {
                    boolean hasConstraints = tf.getConstraints().containsKey("autoIncrement");
                    if(hasConstraints&&tf.getConstraints().get("autoIncrement")){
                        continue;
                    }
                    stmt.setObject(i++, prop);
                } else {
                    Class propCls = prop.getClass();
                    if (!propCls.isArray()) {
                        if(prop==null) continue;
                        Container anno = field.getAnnotation(Container.class);
                        if (anno == null) {
                            DataBase db2 = new DataBase(db.getConfig(), prop.getClass());
                            db2.connect();
                            SqlPut put = new SqlPut(db2, prop.getClass());
                            put.execute(prop);
                            db2.close();
                        } else {
                            Class elemCls = Class.forName(anno.elementType());
                            if (elemCls.getAnnotation(DBTable.class) == null) {
                                throw new DBWriteException(
                                        "Container in a Bean-class must contains another " +
                                                "Bean-class instances with a 'DBTable' annotation"
                                );
                            }
                            Collection coll = (Collection) prop;
                            DataBase db2 = new DataBase(db.getConfig(), elemCls);
                            db2.connect();
                            for (Object elem : coll) {
                                SqlPut put = new SqlPut(db2, elem.getClass());
                                put.execute(elem);
                            }
                            db2.close();
                        }
                    } else {
                        Object[] props = (Object[]) prop;
                        DataBase db2 = new DataBase(db.getConfig(), propCls.getComponentType());
                        db2.connect();
                        for (Object p : props) {
                            SqlPut put = new SqlPut(db2, p.getClass());
                            put.execute(p);
                        }
                        db2.close();
                    }
                }
            }
            return i;
        }catch (DBWriteException e1){
            throw e1;
        }catch (Exception e2){
            throw new DBWriteException(e2);
        }
    }
    private String prepareInsertSql() throws
            KeyNotFoundException,
            ClassNotFoundException {
        TableMeta meta = db.lookUp(cls);
        Predicate<TableField> filter = (field)->{
            Map<String,Boolean> map = field.getConstraints();
            if(map==null){
                return true;
            }
            Boolean autoIncrement = map.get("autoIncrement");
            return !(autoIncrement!=null&&autoIncrement);
        };
        String sql = "INSERT INTO "+meta.getTables().get(0)+"( "+
                meta.getFields().stream()
                .filter(filter)
                .map(field->field.getName())
                .collect(joining(", "))+
                " ) VALUES( "+
                meta.getFields().stream()
                .filter(filter)
                .map(field->"?")
                .collect(joining(", "))+" )";
        return sql;
    }
    private String prepareUpdateSql() throws
            KeyNotFoundException,
            ClassNotFoundException {
        TableMeta meta = db.lookUp(cls);
        String sql = "UPDATE "+meta.getTables().get(0)+" SET\n"+
                meta.getFields().stream()
                .filter((field)->{
                    Map<String,Boolean> map = field.getConstraints();
                    if(map==null){
                        return true;
                    }
                    Boolean iskey = map.get("primaryKey");
                    return !(iskey!=null&&iskey);
                })
                .map(field->field.getName()+" = ?")
                .collect(joining(",\n"))+
                "\nWHERE "+meta.getKey().getName()+" = ?";
        return sql;
    }
}
