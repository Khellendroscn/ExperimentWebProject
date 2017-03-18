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
 * 数据库类
 */
public class DataBase<T> {
    private DBConfig config;
    private Connection conn;
    private Class<T> cls;
    static Map<Class<?>,TableMeta> metaMap = new HashMap<>();

    /**
     * @param config 配置文件
     * @param cls 要加载的类
     * @throws ClassNotFoundException 类不存在异常
     * @throws KeyNotFoundException 主键不存在异常
     */
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

    /**
     * 连接到数据库
     * @throws SQLException sql异常，连接失败
     */
    public void connect() throws SQLException {
        conn = DriverManager.getConnection(config.url,config.user,config.passwd);
    }

    /**
     * 断开连接
     * @throws SQLException sql异常，断开失败
     */
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

    /**
     * 执行Select操作
     * @return SqlSelect对象
     * @see SqlSelect
     * @throws KeyNotFoundException 主键不存在异常
     * @throws ClassNotFoundException 类不存在异常
     */
    public SqlSelect select() throws KeyNotFoundException, ClassNotFoundException {
        return new SqlSelect(this,cls);
    }

    /**
     * 根据主键从数据库中获取实例
     * @param primaryKey 主键值
     * @return 实例
     * @throws KeyNotFoundException 主键不存在异常
     * @throws InvocationTargetException setter方法调用失败
     * @throws SQLException sql异常
     * @throws InstantiationException 实例对象构造失败，请检查Bean是否具有默认构造器
     * @throws IllegalAccessException 非法访问异常，请检查默认构造器是否可访问
     * @throws NoSuchMethodException 方法不存在异常，请检查Bean是否具有setter方法
     * @throws ClassNotFoundException 类不存在异常
     */
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

    /**
     * 获取数据表
     * @return 数据表
     * @throws KeyNotFoundException 主键不存在异常
     * @throws InvocationTargetException setter方法调用失败
     * @throws SQLException sql异常
     * @throws InstantiationException 实例对象构造失败，请检查Bean是否具有默认构造器
     * @throws IllegalAccessException 非法访问异常，请检查默认构造器是否可访问
     * @throws NoSuchMethodException 方法不存在异常，请检查Bean是否具有setter方法
     * @throws ClassNotFoundException 类不存在异常
     */
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

    /**
     * 创建数据表
     * @throws KeyNotFoundException 主键不存在异常
     * @throws ClassNotFoundException 类不存在异常
     * @throws SQLException sql异常
     */
    public void create() throws
            KeyNotFoundException,
            ClassNotFoundException,
            SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute(makeCreateSql());
    }

    /**
     * 获取SqlInsert对象
     * @return SqlInsert对象
     * @throws KeyNotFoundException 主键不存在异常
     * @throws ClassNotFoundException 类不存在异常
     */
    public SqlInsert<T> insert() throws
            KeyNotFoundException,
            ClassNotFoundException {
        if(metaMap.get(cls).getTables().size()!=1)
            throw new UnsupportedOperationException("Insert operation only be supported when the class maped to one table");
        return new SqlInsert<T>(this,cls);
    }

    /**
     * 在数据表中插入一个对象
     * @param obj 要插入的对象
     * @throws KeyNotFoundException 主键不存在异常
     * @throws ClassNotFoundException 类不存在异常
     * @throws InvocationTargetException getter方法调用失败，请检查方法签名
     * @throws SQLException sql异常
     * @throws IllegalAccessException 非法访问异常，请检查getter是否可以访问
     * @throws NoSuchMethodException 方法不存在异常，请检查Bean是否存在getter
     * @throws NoSuchFieldException 字段不存在异常，请检查字段是否缺失注解
     */
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

    /**
     * 从数据表中删除一个对象
     * @param key 要删除的对象的主键
     * @throws SQLException sql异常
     * @throws KeyNotFoundException 主键不存在异常
     * @throws ClassNotFoundException 类不存在异常
     */
    public void delete(Object key) throws
            SQLException,
            KeyNotFoundException,
            ClassNotFoundException {
        if(metaMap.get(cls).getTables().size()!=1)
            throw new UnsupportedOperationException("Delete operation only be supported when the class maped to one table");
        Statement stmt = conn.createStatement();
        stmt.execute(makeDeleteSql(key));
    }

    /**
     * 更新对象信息
     * @param obj 新对象，要求数据库中已存在主键值相同的对象
     * @throws KeyNotFoundException 主键不存在异常
     * @throws ClassNotFoundException 类不存在异常
     * @throws NoSuchFieldException 字段不存在异常，请检查字段是否缺失注解
     * @throws NoSuchMethodException getter方法不存在，请检查getter是否缺失
     * @throws InvocationTargetException getter调用异常，请检查getter方法签名
     * @throws IllegalAccessException 访问权限冲突，请检查getter访问权限
     * @throws SQLException sql异常
     */
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
