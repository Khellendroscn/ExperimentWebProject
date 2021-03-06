package net.khe.db2;

import net.khe.db2.annotations.KeyNotFoundException;

import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by hyc on 2017/3/21.
 */
public class SqlDelete<T> implements SqlWriteOperator{
    private DataBase<T> db;
    private Class<T> cls;
    private Map<Class<?>,String> sqlMap = new HashMap<>();
    private Object key;
    SqlDelete(DataBase<T> db, Class<T> cls) {
        this.db = db;
        this.cls = cls;
    }
    private String prepareSql() throws KeyNotFoundException, ClassNotFoundException {
        TableMeta meta = db.lookUp(cls);
        String sql = "DELETE FROM "+meta.getTables().get(0)+" WHERE "+
                meta.getKey()+" = ?";
        return sql;
    }
    public void setKey(Object key) throws KeyNotFoundException, ClassNotFoundException, NoSuchFieldException {
        TableField keyField = db.lookUp(cls).getKey();
        Class keyType = cls.getDeclaredField(keyField.getNameInClass()).getType();
        if(!keyType.isInstance(key))
            throw new ClassCastException("can't cast param to key's type");
        this.key = key;
    }
    @Override
    public void execute() throws DBWriteException {
        try {
            if (!sqlMap.containsKey(cls))
                sqlMap.put(cls, prepareSql());
            TableMeta meta = db.lookUp(cls);
            String sql = sqlMap.get(cls);
            PreparedStatement stmt = db.getConn().prepareStatement(sql);
            stmt.setObject(1, key);
            stmt.executeUpdate();
        }catch (Exception e){
            throw new DBWriteException(e);
        }
    }
    public void execute(Object key) throws DBWriteException {
        try {
            setKey(key);
            execute();
        }catch (DBWriteException e1){
            throw e1;
        }catch (Exception e2){
            throw new DBWriteException(e2);
        }
    }
}
