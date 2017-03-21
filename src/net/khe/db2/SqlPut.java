package net.khe.db2;

import net.khe.db2.annotations.KeyNotFoundException;
import net.khe.util.ClassVisitor;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
    public void execute(T obj) throws
            IllegalAccessException,
            SQLException,
            KeyNotFoundException,
            ClassNotFoundException,
            NoSuchMethodException,
            InstantiationException,
            InvocationTargetException,
            NoSuchFieldException {
        setObject(obj);
        execute();
    }
    public void execute() throws
            NoSuchFieldException,
            InvocationTargetException,
            IllegalAccessException,
            KeyNotFoundException,
            NoSuchMethodException,
            ClassNotFoundException,
            SQLException,
            InstantiationException {
        if(db.getInstance(getKey())==null){
            insert();
        }else{
            update();
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
    private void insert() throws KeyNotFoundException, ClassNotFoundException, SQLException, NoSuchMethodException, NoSuchFieldException, InstantiationException, IllegalAccessException, InvocationTargetException {
        if(!insertMap.containsKey(cls))
            insertMap.put(cls,prepareInsertSql());
        String sql = insertMap.get(cls);
        PreparedStatement stmt = db.getConn().prepareStatement(sql);
        setStatement(stmt);
        stmt.executeUpdate();
    }
    private void update() throws KeyNotFoundException, ClassNotFoundException, SQLException, NoSuchMethodException, NoSuchFieldException, InstantiationException, IllegalAccessException, InvocationTargetException {
        if(!updateMap.containsKey(cls))
            updateMap.put(cls,prepareUpdateSql());
        String sql = updateMap.get(cls);
        PreparedStatement stmt = db.getConn().prepareStatement(sql);
        int i = setStatement(stmt);
        stmt.setObject(i,getKey());
        stmt.executeUpdate();
    }
    private int setStatement(PreparedStatement stmt) throws KeyNotFoundException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, SQLException, NoSuchFieldException, InstantiationException {
        TableMeta meta = db.lookUp(cls);
        int i=1;
        for(Field field:cls.getDeclaredFields()){
            ClassVisitor visitor = new ClassVisitor(cls);
            Method getter = visitor.getGetter(field);
            Object prop = getter.invoke(obj);
            if(meta.getField(field.getName())!=null){
                stmt.setObject(i++,prop);
            }else{
                if(!prop.getClass().isArray()){
                    DataBase db2 = new DataBase(db.getConfig(),prop.getClass());
                    db2.connect();
                    SqlPut put = new SqlPut(db2,prop.getClass());
                    put.execute(prop);
                    db2.close();
                }else{
                    Object[] props = (Object[])prop;
                    for(Object p:props){
                        DataBase db2 = new DataBase(db.getConfig(),p.getClass());
                        db2.connect();
                        SqlPut put = new SqlPut(db2,p.getClass());
                        put.execute(p);
                        db2.close();
                    }
                }
            }
        }
        return i;
    }
    private String prepareInsertSql() throws
            KeyNotFoundException,
            ClassNotFoundException {
        TableMeta meta = db.lookUp(cls);
        String sql = "INSERT INTO "+meta.getTables().get(0)+"( "+
                meta.getFields().stream()
                .map(field->field.getName())
                .collect(joining(", "))+
                " ) VALUES( "+
                meta.getFields().stream()
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
                .map(field->field.getName()+" = ?")
                .collect(joining(",\n"))+
                "\nWHERE "+meta.getKey().getName()+" = ?";
        return sql;
    }
}
