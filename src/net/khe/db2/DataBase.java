package net.khe.db2;

import net.khe.db2.annotations.DBAnnotationsProcesser;
import net.khe.db2.annotations.KeyNotFoundException;
import net.khe.util.ClassVisitor;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by hyc on 2017/3/17.
 */
public class DataBase<T> {
    private DBConfig config;
    private Connection conn;
    private Class<T> cls;
    static Map<Class<?>,TableMeta> metaMap = new HashMap<>();
    public DataBase(DBConfig config, Class<T> cls) throws ClassNotFoundException, KeyNotFoundException {
        Class.forName(config.driver);
        this.config = config;
        this.cls = cls;
        if(!metaMap.containsKey(cls)){
            DBAnnotationsProcesser<T> processer =
                    new DBAnnotationsProcesser<T>(cls);
            metaMap.put(cls,processer.getMeta());
        }
    }
    public void connect() throws SQLException {
        conn = DriverManager.getConnection(config.url,config.user,config.passwd);
    }
    public void close() throws SQLException {
        conn.close();
    }
    Connection getConn(){return conn;}
    public static <T>TableMeta lookUp(Class<T> cls) throws KeyNotFoundException, ClassNotFoundException {
        if(!metaMap.containsKey(cls)){
            DBAnnotationsProcesser<T> processer =
                    new DBAnnotationsProcesser<T>(cls);
            metaMap.put(cls,processer.getMeta());
        }
        return metaMap.get(cls);
    }

    public SqlSelect select() throws KeyNotFoundException, ClassNotFoundException {
        return new SqlSelect(this,cls);
    }
    public T getInstance(Object primaryKey) throws
            KeyNotFoundException,
            InvocationTargetException,
            SQLException,
            InstantiationException,
            IllegalAccessException,
            NoSuchMethodException,
            ClassNotFoundException {
        String key1 = lookUp(cls).getKey().getName();
        String key2 = primaryKey.toString();
        if(primaryKey instanceof CharSequence){
            key2 = String.format("\'%s\'",key2);
        }
        SqlSelect<T> select = new SqlSelect<T>(this,cls);
        select.where(key1+" = "+key2);
        Table<T> tb = select.execute();
        return tb.getList().get(0);
    }
    public Table<T> getTable() throws
            KeyNotFoundException,
            InvocationTargetException,
            SQLException,
            InstantiationException,
            IllegalAccessException,
            NoSuchMethodException,
            ClassNotFoundException {
        return select().execute();
    }
    public void create() throws
            KeyNotFoundException,
            ClassNotFoundException,
            SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute(makeCreateSql());
    }
    public SqlInsert<T> insert() throws
            KeyNotFoundException,
            ClassNotFoundException {
        if(metaMap.get(cls).getTables().size()!=1)
            throw new UnsupportedOperationException("Insert operation only be supported when the class maped to one table");
        return new SqlInsert<T>(this,cls);
    }
    public void insert(T obj) throws
            KeyNotFoundException,
            ClassNotFoundException,
            InvocationTargetException,
            SQLException,
            IllegalAccessException,
            NoSuchMethodException,
            NoSuchFieldException {
        insert().execute(obj);
    }
    public void delete(Object key) throws
            SQLException,
            KeyNotFoundException,
            ClassNotFoundException {
        if(metaMap.get(cls).getTables().size()!=1)
            throw new UnsupportedOperationException("Delete operation only be supported when the class maped to one table");
        Statement stmt = conn.createStatement();
        stmt.execute(makeDeleteSql(key));
    }
    public void update(T obj) throws
            KeyNotFoundException,
            ClassNotFoundException,
            NoSuchFieldException,
            NoSuchMethodException,
            InvocationTargetException,
            IllegalAccessException,
            SQLException {
        TableMeta meta = lookUp(cls);
        if(meta.getTables().size()!=1)
            throw new UnsupportedOperationException("Update operation only be supported when the class maped to one table");
        SqlUpdate<T> update = new SqlUpdate<T>(this,cls);
        update.execute(obj);
    }
    private String makeCreateSql() throws KeyNotFoundException, ClassNotFoundException {
        TableMeta meta = lookUp(cls);
        String sql = "CREATE TABLE "+meta.getTables().get(0)+"(\n"+
                meta.getFields().stream()
                .map(field->field.toSql())
                .collect(Collectors.joining(",\n"))+
                "\n)";
        return sql;
    }
    private String makeDeleteSql(Object key) throws
            KeyNotFoundException,
            ClassNotFoundException {
        TableMeta meta = lookUp(cls);
        String value = key.toString();
        if(key instanceof CharSequence)
            value = String.format("\'%s\'",value);
        String sql = "DELETE FROM "+meta.getTables().get(0)+
                "\nWHERE "+meta.getKey().getName()+
                " = "+value;
        return sql;
    }
}
