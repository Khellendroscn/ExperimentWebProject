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
    protected DBConfig config;
    protected Connection conn;
    protected Class<T> cls;
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
        if(conn==null)
            conn = DriverManager.getConnection(config.url,config.user,config.passwd);
    }

    /**
     * 断开连接
     * @throws SQLException sql异常，断开失败
     */
    public void close() throws SQLException {
        conn.close();
    }
    @Override
    public void finalize(){
        try {
            close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
     * @return 数据库配置信息
     */
    public DBConfig getConfig(){return config;}
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
        if(tb.getList().size()>0)return tb.getList().get(0);
        else return null;
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
     * 从数据表中删除一个对象
     * @param key
     * @throws SQLException
     * @throws KeyNotFoundException
     * @throws ClassNotFoundException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     */
    public void delete(Object key) throws
            SQLException,
            KeyNotFoundException,
            ClassNotFoundException,
            InvocationTargetException,
            NoSuchMethodException,
            InstantiationException,
            IllegalAccessException,
            NoSuchFieldException {
        if(metaMap.get(cls).getTables().size()!=1)
            throw new UnsupportedOperationException("Delete operation only be supported when the class maped to one table");
        SqlDelete<T> delete = new SqlDelete<T>(this,cls);
        delete.execute(key);
    }

    /**
     * 将对象存入数据库，如果对象key不存在则insert，如果已存在则update
     * @param obj
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws InstantiationException
     * @throws KeyNotFoundException
     * @throws SQLException
     * @throws NoSuchFieldException
     * @throws NoSuchMethodException
     * @throws ClassNotFoundException
     */
    public void put(T obj) throws
            IllegalAccessException,
            InvocationTargetException,
            InstantiationException,
            KeyNotFoundException,
            SQLException,
            NoSuchFieldException,
            NoSuchMethodException,
            ClassNotFoundException {
        if(metaMap.get(cls).getTables().size()!=1)
            throw new UnsupportedOperationException("Write operation only be supported when the class maped to one table");
        SqlPut<T> put = new SqlPut<T>(this,cls);
        put.execute(obj);
    }

    /**
     * 新建一个事务对象
     * @return 数据库事务对象
     */
    public DBSession<T> createSession(){
        return new DBSession<T>(this,cls);
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
}
