package net.khe.db2;

import net.khe.db2.annotations.KeyNotFoundException;

import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hyc on 2017/3/21.
 * 数据库事务对象
 * 可以将一组写入操作加入事务列表，
 * 只要有一个操作抛出异常，事务就会进行回滚
 */
public class DBSession<T> {
    private DataBase<T> db;
    private Class<T> cls;
    private List<SqlWriteOperator> operators = new ArrayList<>();
    DBSession(DataBase<T> db, Class<T> cls){
        this.db = db;
        this.cls = cls;
    }

    /**
     * 在事务中添加一个put操作
     * @param obj 要加入数据库的对象
     * @see SqlPut
     */
    public void put(T obj){
        SqlPut<T> put = new SqlPut<T>(db,cls);
        put.setObject(obj);
        operators.add(put);
    }

    /**
     * 在事务中添加一个删除操作
     * @param key 将要删除的对象的键值
     * @throws ClassNotFoundException
     * @throws NoSuchFieldException
     * @throws KeyNotFoundException
     */
    public void delete(Object key) throws
            ClassNotFoundException,
            NoSuchFieldException,
            KeyNotFoundException {
        SqlDelete<T> del = new SqlDelete<T>(db,cls);
        del.setKey(key);
        operators.add(del);
    }

    /**
     * 执行事务，该操作是同步的
     * @throws SQLException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws KeyNotFoundException
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws NoSuchFieldException
     */
    public synchronized void execute() throws
            SQLException,
            IllegalAccessException,
            InstantiationException,
            KeyNotFoundException,
            ClassNotFoundException,
            NoSuchMethodException,
            InvocationTargetException,
            NoSuchFieldException {
        Connection conn = db.getConn();
        Savepoint savepoint = conn.setSavepoint("session");
        try{
            for(SqlWriteOperator operator:operators){
                operator.execute();
            }
            conn.releaseSavepoint(savepoint);
        }catch (Exception e){
            conn.rollback(savepoint);
            throw e;
        }
    }
}
