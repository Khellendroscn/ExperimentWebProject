package net.khe.db2;

import net.khe.db2.annotations.DBAnnotationsProcesser;
import net.khe.db2.annotations.KeyNotFoundException;
import net.khe.util.Factory;

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
    public SqlSelect select() throws DBQuaryException {
        return new SqlSelect(this,cls);
    }

    /**
     * 根据主键从数据库中获取实例
     * @param primaryKey 主键值
     * @return 实例
     * @throws DBQuaryException 数据库查询异常
     */
    public T getInstance(Object primaryKey) throws DBQuaryException {
        try {
            SqlSelect<T> sel = new SqlSelect<T>(this,cls);
            String value = primaryKey.toString();
            if(primaryKey instanceof CharSequence){
                value = String.format("'%s'",value);
            }
            sel.where(lookUp(cls).getKey().getName()+" = "+value);
            return sel.query().next();
        } catch (Exception e){
            throw new DBQuaryException(e);
        }
    }

    /**
     * 获取数据表（已过时，现在使用quary方法作为代替）
     * @return 数据表
     * @throws DBQuaryException 数据库查询异常
     */
    @Deprecated
    public Table<T> getTable() throws DBQuaryException {
        return select().execute();
    }

    /**
     * 创建数据表
     * @throws DBWriteException 数据库写入异常
     */
    public void create() throws DBWriteException {
        try {
            Statement stmt = conn.createStatement();
            stmt.execute(makeCreateSql());
        }catch (Exception e){
            throw new DBWriteException(e);
        }
    }

    /**
     * 从数据表中删除对象
     * @param key 对象主键值
     * @throws DBWriteException 数据库写入异常
     */
    public void delete(Object key) throws DBWriteException {
        if(metaMap.get(cls).getTables().size()!=1)
            throw new UnsupportedOperationException("Delete operation only be supported when the class maped to one table");
        SqlDelete<T> delete = new SqlDelete<T>(this,cls);
        delete.execute(key);
    }

    /**
     * 将对象存入数据库，如果对象key不存在则insert，如果已存在则update
     * @param obj
     * @throws DBWriteException 数据库写入异常
     */
    public void put(T obj) throws DBWriteException {
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
        String sql = "CREATE TABLE IF NOT EXISTS "+meta.getTables().get(0)+"(\n"+
                meta.getFields().stream()
                .map(field->field.toSql())
                .collect(Collectors.joining(",\n"))+
                "\n)";
        return sql;
    }

    /**
     * 查询数据表，返回一个生成器
     * @see DBQuery
     * @param factory 可选工厂对象，如果传入工厂对象则使用工厂来创建对象，否则使用Class.newInstance方法
     * @return DBQuary对象
     * @throws DBQuaryException 数据库查询异常
     */
    public DBQuery<T> query(Factory<T> factory) throws DBQuaryException {
        SqlSelect<T> sel = new SqlSelect<T>(this,cls);
        ResultSet rs = sel.getResultSet();
        return new ObjectQuery<T>(rs,cls,this,factory);
    }

    /**
     * 查询数据表，返回一个生成器
     * @see DBQuery
     * @return DBQuary对象
     * @throws DBQuaryException 数据库查询异常
     */
    public DBQuery<T> query() throws DBQuaryException {
        SqlSelect<T> sel = new SqlSelect<T>(this,cls);
        ResultSet rs = sel.getResultSet();
        return new ObjectQuery<T>(rs,this,cls);
    }
}
