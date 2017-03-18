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
