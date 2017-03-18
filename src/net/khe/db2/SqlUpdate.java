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
 * 表示sql更新操作
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

    /**
     * 执行更新操作
     * @param obj 新对象
     * @throws SQLException sql异常
     * @throws KeyNotFoundException 主键不存在异常
     * @throws ClassNotFoundException 类不存在异常
     * @throws NoSuchFieldException 字段不存在异常，请检查字段是否缺少注解
     * @throws NoSuchMethodException getter不存在异常，请检查是否缺少getter
     * @throws InvocationTargetException getter调用异常，请检查getter的方法签名
     * @throws IllegalAccessException 访问权限冲突，请检查getter是否可以访问
     */
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
