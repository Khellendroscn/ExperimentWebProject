package net.khe.db2;

import net.khe.db2.annotations.KeyNotFoundException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by hyc on 2017/3/21.
 * 数据库连接池
 */
public class DBPool {
    private Map<String,BlockingDeque<Connection>> freeConns =
            new HashMap<>();

    /**
     * 返回一个特殊的数据库对象，该对象连接时优先从连接池中获取连接，关闭时将连接释放到连接池
     * @param config 数据库配置信息
     * @param cls 映射到数据库的类
     * @return 新数据库
     * @throws KeyNotFoundException
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public <T>DataBase<T> newDataBase(DBConfig config,Class<T> cls) throws KeyNotFoundException, ClassNotFoundException, SQLException {
        if (!freeConns.containsKey(config.url)){
            freeConns.put(config.url,new LinkedBlockingDeque<>());
        }
        DataBase<T> db = new DataBaseInPool<T>(config,cls);
        db.connect();
        return db;
    }

    /**
     * 关闭所有连接
     * @throws SQLException
     */
    public void close() throws SQLException {
        for(String url:freeConns.keySet()){
            while (!freeConns.get(url).isEmpty()){
                freeConns.get(url).pop().close();
            }
        }
    }
    @Override
    public void finalize (){
        try {
            close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    class DataBaseInPool<T> extends DataBase<T>{
        /**
         * @param config 配置文件
         * @param cls    要加载的类
         * @throws ClassNotFoundException 类不存在异常
         * @throws KeyNotFoundException   主键不存在异常
         */
        public DataBaseInPool(DBConfig config, Class<T> cls) throws ClassNotFoundException, KeyNotFoundException {
            super(config, cls);
        }
        @Override
        public void connect() throws SQLException {
            if(conn!=null)return;
            if(freeConns.get(config.url).isEmpty()){
                conn = DriverManager.getConnection(config.url,config.user,config.passwd);
            }else{
                conn = freeConns.get(config.url).pop();
            }
        }
        @Override
        public void close(){
            freeConns.get(config.url).push(conn);
            conn = null;
        }
    }
}
