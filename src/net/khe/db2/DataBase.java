package net.khe.db2;

import net.khe.db2.annotations.DBAnnotationsProcesser;
import net.khe.db2.annotations.KeyNotFoundException;
import net.khe.util.Factory;

import java.sql.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by hyc on 2017/3/17.
 * ���ݿ���
 */
public class DataBase<T> {
    protected DBConfig config;
    protected Connection conn;
    protected Class<T> cls;
    static Map<Class<?>,TableMeta> metaMap = new HashMap<>();

    /**
     * @param config �����ļ�
     * @param cls Ҫ���ص���
     * @throws ClassNotFoundException �಻�����쳣
     * @throws KeyNotFoundException �����������쳣
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
     * ���ӵ����ݿ�
     * @throws SQLException sql�쳣������ʧ��
     */
    public void connect() throws SQLException {
        if(conn==null)
            conn = DriverManager.getConnection(config.url,config.user,config.passwd);
    }

    /**
     * �Ͽ�����
     * @throws SQLException sql�쳣���Ͽ�ʧ��
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
     * @return ���ݿ�������Ϣ
     */
    public DBConfig getConfig(){return config;}
    /**
     * ִ��Select����
     * @return SqlSelect����
     * @see SqlSelect
     * @throws KeyNotFoundException �����������쳣
     * @throws ClassNotFoundException �಻�����쳣
     */
    public SqlSelect select() throws DBQuaryException {
        return new SqlSelect(this,cls);
    }

    /**
     * �������������ݿ��л�ȡʵ��
     * @param primaryKey ����ֵ
     * @return ʵ��
     * @throws DBQuaryException ���ݿ��ѯ�쳣
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
     * ��ȡ���ݱ��ѹ�ʱ������ʹ��quary������Ϊ���棩
     * @return ���ݱ�
     * @throws DBQuaryException ���ݿ��ѯ�쳣
     */
    @Deprecated
    public Table<T> getTable() throws DBQuaryException {
        return select().execute();
    }

    /**
     * �������ݱ�
     * @throws DBWriteException ���ݿ�д���쳣
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
     * �����ݱ���ɾ������
     * @param key ��������ֵ
     * @throws DBWriteException ���ݿ�д���쳣
     */
    public void delete(Object key) throws DBWriteException {
        if(metaMap.get(cls).getTables().size()!=1)
            throw new UnsupportedOperationException("Delete operation only be supported when the class maped to one table");
        SqlDelete<T> delete = new SqlDelete<T>(this,cls);
        delete.execute(key);
    }

    /**
     * ������������ݿ⣬�������key��������insert������Ѵ�����update
     * @param obj
     * @throws DBWriteException ���ݿ�д���쳣
     */
    public void put(T obj) throws DBWriteException {
        if(metaMap.get(cls).getTables().size()!=1)
            throw new UnsupportedOperationException("Write operation only be supported when the class maped to one table");
        SqlPut<T> put = new SqlPut<T>(this,cls);
        put.execute(obj);
    }

    /**
     * �½�һ���������
     * @return ���ݿ��������
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
     * ��ѯ���ݱ�����һ��������
     * @see DBQuery
     * @param factory ��ѡ��������������빤��������ʹ�ù������������󣬷���ʹ��Class.newInstance����
     * @return DBQuary����
     * @throws DBQuaryException ���ݿ��ѯ�쳣
     */
    public DBQuery<T> query(Factory<T> factory) throws DBQuaryException {
        SqlSelect<T> sel = new SqlSelect<T>(this,cls);
        ResultSet rs = sel.getResultSet();
        return new ObjectQuery<T>(rs,cls,this,factory);
    }

    /**
     * ��ѯ���ݱ�����һ��������
     * @see DBQuery
     * @return DBQuary����
     * @throws DBQuaryException ���ݿ��ѯ�쳣
     */
    public DBQuery<T> query() throws DBQuaryException {
        SqlSelect<T> sel = new SqlSelect<T>(this,cls);
        ResultSet rs = sel.getResultSet();
        return new ObjectQuery<T>(rs,this,cls);
    }
}
