package net.khe.db2;

import net.khe.db2.annotations.KeyNotFoundException;
import net.khe.util.ClassVisitor;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by hyc on 2017/3/17.
 */
public class Table<T> implements Iterable<T> {
    private Class<T> cls;
    private DataBase<T> db;
    private ResultSet rs;
    private TableMeta meta;
    private List<T> list = new ArrayList<T>();

    public Table(DataBase<T> db, ResultSet rs, Class<T> cls)
            throws
            NoSuchMethodException,
            InstantiationException,
            SQLException,
            IllegalAccessException,
            InvocationTargetException,
            KeyNotFoundException,
            ClassNotFoundException {
        this.rs = rs;
        this.cls = cls;
        this.db = db;
        this.meta = DataBase.metaMap.get(cls);
        readAll();
    }
    private T readObj() throws
            IllegalAccessException,
            InstantiationException,
            SQLException,
            NoSuchMethodException,
            InvocationTargetException,
            KeyNotFoundException,
            ClassNotFoundException {
        ClassVisitor<T> visitor = new ClassVisitor<T>(cls);
        T instance = cls.newInstance();
        for(Field field:cls.getDeclaredFields()){
            TableField tf = meta.getField(field.getName());
            if(tf!=null){
                Object obj = rs.getObject(tf.getName());
                visitor.getSetter(field).invoke(instance,obj);
            }else{
                Class cls2 = field.getClass();
                TableField key1 = meta.getKey();
                boolean isArr = cls2.isArray();
                if(isArr) cls2 = cls2.getComponentType();
                TableField key2 = db.lookUp(cls2).getForeigns(cls);
                SqlSelect select = new SqlSelect(db,cls2);
                Object value = rs.getObject(key1.getName());
                String str = value.toString();
                if(value instanceof CharSequence){
                    str = String.format("\'%s\'",str);
                }
                select.where(key2.getName()+" = "+str);
                Table tb = select.execute();
                if(isArr){
                    Object[] arr = tb.getList().toArray();
                    visitor.getSetter(field).invoke(instance,arr);
                }else{
                    Object obj = tb.getList().get(0);
                    visitor.getSetter(field).invoke(instance,obj);
                }
            }
        }
        list.add(instance);
        return instance;
    }
    private void readAll() throws
            IllegalAccessException,
            InvocationTargetException,
            InstantiationException,
            KeyNotFoundException,
            SQLException,
            NoSuchMethodException,
            ClassNotFoundException {
        while (rs.next())
            readObj();
    }
    public List<T> getList(){
        return list;
    }
    @Override
    public Iterator<T> iterator() {
        return list.iterator();
    }
    @Override
    public String toString(){
        return list.toString();
    }
}
