package net.khe.db2;

import net.khe.db2.annotations.KeyNotFoundException;
import net.khe.util.ClassVisitor;

import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by hyc on 2017/3/18.
 */
public class SqlUpdate<T> {
    private DataBase<T> db;
    private Class<T> cls;
    static Map<Class<?>,String> sqlMap = new HashMap<>();
    public SqlUpdate(DataBase<T> db,Class<T> cls) throws KeyNotFoundException, ClassNotFoundException {
        this.db = db;
        this.cls = cls;
        if(!sqlMap.containsKey(cls)){
            sqlMap.put(cls,prepareSql());
        }
    }
    public void execute(T obj) throws
            SQLException,
            KeyNotFoundException,
            ClassNotFoundException,
            NoSuchFieldException,
            NoSuchMethodException,
            InvocationTargetException,
            IllegalAccessException {
        PreparedStatement stmt = db.getConn().prepareStatement(sqlMap.get(cls));
        ClassVisitor visitor = new ClassVisitor(obj.getClass());
        TableMeta meta = db.lookUp(cls);
        int i =1;
        for(TableField field:meta.getFields()){
            Object prop = visitor
                    .getGetter(cls.getDeclaredField(field.getNameInClass()))
                    .invoke(obj);
            stmt.setObject(i++,prop);
        }
        Object key = visitor.getGetter(cls.getDeclaredField(meta.getKey().getNameInClass()))
                .invoke(obj);
        stmt.setObject(i,key);
        stmt.executeUpdate();
    }
    private String prepareSql() throws
            KeyNotFoundException,
            ClassNotFoundException {
        TableMeta meta = db.lookUp(cls);
        String sql = "UPDATE "+meta.getTables().get(0)+" SET \n"+
                meta.getFields().stream()
                .map(field->field.getName()+" = ?")
                .collect(Collectors.joining(",\n"))+
                "WHERE "+meta.getKey().getName()+" = ?";
        return sql;
    }
}
