package net.khe.db2;

import net.khe.db2.annotations.KeyNotFoundException;
import net.khe.util.ClassVisitor;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import static java.util.stream.Collectors.*;

/**
 * Created by hyc on 2017/3/18.
 * 表示Sql插入操作的类
 */
public class SqlInsert<T> {
    private DataBase<T> db;
    private Class<T> cls;
    static Map<Class<?>,String> sqlMap = new HashMap<>();

    SqlInsert(DataBase<T> db,Class<T> cls) throws
            KeyNotFoundException,
            ClassNotFoundException {
        this.db = db;
        this.cls = cls;
        if(!sqlMap.containsKey(cls)){
            sqlMap.put(cls,prepareSql());
        }
    }

    /**
     * 执行插入操作
     * @param obj 要插入的对象
     * @throws SQLException sql异常
     * @throws KeyNotFoundException 主键不存在异常
     * @throws ClassNotFoundException 类不存在异常
     * @throws NoSuchFieldException 字段不存在异常，请检查字段是否缺失注解
     * @throws NoSuchMethodException getter不存在异常，请检查是否缺失getter方法
     * @throws InvocationTargetException 方法调用失败异常，请检查getter方法签名
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
        TableMeta meta = db.lookUp(cls);
        ClassVisitor<T> visitor = new ClassVisitor(obj.getClass());
        int i = 1;
        for(TableField field:meta.getFields()){
            Field f = cls.getDeclaredField(field.getNameInClass());
            Object prop = visitor.getGetter(f).invoke(obj);
            stmt.setObject(i++,prop);
        }
        stmt.executeUpdate();
    }
    private String prepareSql() throws
            KeyNotFoundException,
            ClassNotFoundException {
        TableMeta meta = db.lookUp(cls);
        String sql = "INSERT INTO "+meta.getTables().get(0)+"( "+
                meta.getFields().stream()
                .map(field->field.getName())
                .collect(joining(", "))+
                ")\nVALUES( "+
                meta.getFields().stream()
                .map(field->"?")
                .collect(joining(", "))+
                " )";
        return sql;
    }
}
