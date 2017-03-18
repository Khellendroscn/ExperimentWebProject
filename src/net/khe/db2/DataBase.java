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
 * ���ݿ���
 */
public class DataBase<T> {
    private DBConfig config;
    private Connection conn;
    private Class<T> cls;
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
        conn = DriverManager.getConnection(config.url,config.user,config.passwd);
    }

    /**
     * �Ͽ�����
     * @throws SQLException sql�쳣���Ͽ�ʧ��
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
     * ִ��Select����
     * @return SqlSelect����
     * @see SqlSelect
     * @throws KeyNotFoundException �����������쳣
     * @throws ClassNotFoundException �಻�����쳣
     */
    public SqlSelect select() throws KeyNotFoundException, ClassNotFoundException {
        return new SqlSelect(this,cls);
    }

    /**
     * �������������ݿ��л�ȡʵ��
     * @param primaryKey ����ֵ
     * @return ʵ��
     * @throws KeyNotFoundException �����������쳣
     * @throws InvocationTargetException setter��������ʧ��
     * @throws SQLException sql�쳣
     * @throws InstantiationException ʵ��������ʧ�ܣ�����Bean�Ƿ����Ĭ�Ϲ�����
     * @throws IllegalAccessException �Ƿ������쳣������Ĭ�Ϲ������Ƿ�ɷ���
     * @throws NoSuchMethodException �����������쳣������Bean�Ƿ����setter����
     * @throws ClassNotFoundException �಻�����쳣
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
     * ��ȡ���ݱ�
     * @return ���ݱ�
     * @throws KeyNotFoundException �����������쳣
     * @throws InvocationTargetException setter��������ʧ��
     * @throws SQLException sql�쳣
     * @throws InstantiationException ʵ��������ʧ�ܣ�����Bean�Ƿ����Ĭ�Ϲ�����
     * @throws IllegalAccessException �Ƿ������쳣������Ĭ�Ϲ������Ƿ�ɷ���
     * @throws NoSuchMethodException �����������쳣������Bean�Ƿ����setter����
     * @throws ClassNotFoundException �಻�����쳣
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
     * �������ݱ�
     * @throws KeyNotFoundException �����������쳣
     * @throws ClassNotFoundException �಻�����쳣
     * @throws SQLException sql�쳣
     */
    public void create() throws
            KeyNotFoundException,
            ClassNotFoundException,
            SQLException {
        Statement stmt = conn.createStatement();
        stmt.execute(makeCreateSql());
    }

    /**
     * ��ȡSqlInsert����
     * @return SqlInsert����
     * @throws KeyNotFoundException �����������쳣
     * @throws ClassNotFoundException �಻�����쳣
     */
    public SqlInsert<T> insert() throws
            KeyNotFoundException,
            ClassNotFoundException {
        if(metaMap.get(cls).getTables().size()!=1)
            throw new UnsupportedOperationException("Insert operation only be supported when the class maped to one table");
        return new SqlInsert<T>(this,cls);
    }

    /**
     * �����ݱ��в���һ������
     * @param obj Ҫ����Ķ���
     * @throws KeyNotFoundException �����������쳣
     * @throws ClassNotFoundException �಻�����쳣
     * @throws InvocationTargetException getter��������ʧ�ܣ����鷽��ǩ��
     * @throws SQLException sql�쳣
     * @throws IllegalAccessException �Ƿ������쳣������getter�Ƿ���Է���
     * @throws NoSuchMethodException �����������쳣������Bean�Ƿ����getter
     * @throws NoSuchFieldException �ֶβ������쳣�������ֶ��Ƿ�ȱʧע��
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
     * �����ݱ���ɾ��һ������
     * @param key Ҫɾ���Ķ��������
     * @throws SQLException sql�쳣
     * @throws KeyNotFoundException �����������쳣
     * @throws ClassNotFoundException �಻�����쳣
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
     * ���¶�����Ϣ
     * @param obj �¶���Ҫ�����ݿ����Ѵ�������ֵ��ͬ�Ķ���
     * @throws KeyNotFoundException �����������쳣
     * @throws ClassNotFoundException �಻�����쳣
     * @throws NoSuchFieldException �ֶβ������쳣�������ֶ��Ƿ�ȱʧע��
     * @throws NoSuchMethodException getter���������ڣ�����getter�Ƿ�ȱʧ
     * @throws InvocationTargetException getter�����쳣������getter����ǩ��
     * @throws IllegalAccessException ����Ȩ�޳�ͻ������getter����Ȩ��
     * @throws SQLException sql�쳣
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
