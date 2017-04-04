package net.khe.db2;

import net.khe.db2.annotations.Container;
import net.khe.db2.annotations.DBTable;
import net.khe.util.ClassVisitor;
import net.khe.util.Factory;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * DBQuery接口的实现类，以对象的方式返回表中数据
 * @param <T> 生成的对象类型
 */
public class ObjectQuery<T> extends AbstractDBQuery<T> {
    private DataBase db;
    private Class<T> cls = null;
    private Factory<T> factory = null;
    private TableMeta meta;
    private List<Field> fields;
    ObjectQuery(ResultSet rs, DataBase db, Class<T> cls) throws DBQuaryException {
        super(rs);
        this.db = db;
        this.cls = cls;
        try {
            meta = DataBase.lookUp(cls);
        } catch (Exception e){
            throw new DBQuaryException(e);
        }
        initFields();
    }
    ObjectQuery(ResultSet rs, Class<T> cls, DataBase db, Factory<T> factory) throws DBQuaryException {
        this(rs, db, cls);
        this.factory = factory;
    }
    @Override
    public T next() {
        try {
            if(!rs.next()) return null;
        } catch (SQLException e) {
            throw new RuntimeException(new DBQuaryException(e));
        }
        T instance = initObj();
        try{
            for(Field field:fields){
                if(meta.getField(field.getName())!=null){
                    readField(instance,field);
                }else if(field.getType().isArray()){
                    Class elemCls = field.getType().getComponentType();
                    if(elemCls.getAnnotation(DBTable.class)==null){
                        throw new DBQuaryException(
                                "Array in Bean-class must contains another " +
                                "Bean-class instances with a 'DBTable' annotation"
                        );
                    }
                    readArray(instance,field);
                }else{
                    if(field.getAnnotation(Container.class)!=null){
                        readCollection(instance,field);
                    }else{
                        Class propCls = field.getType();
                        if(propCls.getAnnotation(DBTable.class)==null){
                            throw new DBQuaryException(
                                    "bean in Bean-class must has a 'DBTable' annotation"
                            );
                        }
                        readBean(instance,field);
                    }
                }
            }
            return instance;
        } catch (DBQuaryException e) {
            throw new RuntimeException(e);
        }
    }
    private  void initFields(){
        fields = new ArrayList<Field>(Arrays.asList(cls.getDeclaredFields()));
        fields.sort(new Comparator<Field>() {
            @Override
            public int compare(Field f1, Field f2) {
                boolean f1InMeta = meta.getField(f1.getName())!=null;
                boolean f2InMeta = meta.getField(f2.getName())!=null;
                if(f1InMeta&&!f2InMeta){
                    return -1;
                }else if(!f1InMeta&&f2InMeta){
                    return 1;
                }else{
                    return f1.getName().compareTo(f2.getName());
                }
            }
        });
    }
    private T initObj(){
        if(factory!=null){
            return factory.create();
        }else{
            try {
                return cls.newInstance();
            } catch (Exception e){
                throw new RuntimeException(
                        new DBQuaryException(e)
                );
            }
        }
    }
    private void readField(T instance, Field field) throws DBQuaryException {
        try {
            TableField tf = meta.getField(field.getName());
            ClassVisitor<T> visitor = new ClassVisitor<T>(cls);
            Method setter = visitor.getSetter(field);
            Object prop = rs.getObject(tf.getName());
            setter.invoke(instance,prop);
        } catch (Exception e) {
            throw new DBQuaryException(e);
        }
    }
    private void readBean(T instance, Field field) throws DBQuaryException {
        try{
            ClassVisitor<T> visitor = new ClassVisitor<T>(cls);
            Method setter = visitor.getSetter(field);
            Class propCls = field.getType();
            TableMeta propMeta = DataBase.lookUp(propCls);
            DataBase db2 = new DataBase(db.config, propCls);
            db2.connect();
            SqlSelect sel = new SqlSelect(db2,propCls);
            sel.where(whereSql(instance,propMeta.getForeign(cls)));
            DBQuery query = new ObjectQuery(sel.getResultSet(),db2,propCls);
            Object prop = query.next();
            if(prop!=null){
                setter.invoke(instance,prop);
            }
            db2.close();
        }catch (Exception e){
            throw new DBQuaryException(e);
        }
    }
    private void readArray(T instance, Field field) throws DBQuaryException {
        try{
            ClassVisitor<T> visitor = new ClassVisitor<T>(cls);
            Method setter = visitor.getSetter(field);
            Class elemCls = field.getType().getComponentType();
            TableMeta elemMeta = DataBase.lookUp(elemCls);
            if(elemCls.isArray()) {
                throw new DBQuaryException(
                        "Only Expected 1d array in the bean."
                );
            }
            DataBase db2 = new DataBase(db.config,elemCls);
            db2.connect();
            SqlSelect sel = new SqlSelect(db2,elemCls);
            sel.where(whereSql(instance, elemMeta.getForeign(cls)));
            ResultSet rs2 = sel.getResultSet();
            rs2.last();
            int size = rs2.getRow();
            rs2.beforeFirst();
            DBQuery query = new ObjectQuery<T>(rs2,db2,elemCls);
            Object arr = Array.newInstance(elemCls,size);
            Object elem = null;
            for(int i=0;i<size;++i){
                Array.set(arr,i,query.next());
            }
            setter.invoke(instance,arr);
            db2.close();
        }catch (DBQuaryException e1){
            throw e1;
        } catch (Exception e2){
            throw new DBQuaryException(e2);
        }
    }
    private void readCollection(T instance, Field field) throws DBQuaryException {
        try {
            ClassVisitor<T> visitor = new ClassVisitor<T>(cls);
            Method setter = visitor.getSetter(field);
            Container anno = field.getAnnotation(Container.class);
            Class containerCls = anno.containerType();
            Class elemCls = anno.elementType();
            if(elemCls.getAnnotation(DBTable.class)==null){
                throw new DBQuaryException(
                        "Container in a Bean-class must contains another " +
                                "Bean-class instances with a 'DBTable' annotation"
                );
            }
            java.util.Collection coll = (Collection) containerCls.newInstance();
            TableMeta elemMeta = DataBase.lookUp(elemCls);
            DataBase db2 = new DataBase(db.config,elemCls);
            db2.connect();
            SqlSelect sel = new SqlSelect(db2,elemCls);
            sel.where(whereSql(instance, elemMeta.getForeign(cls)));
            DBQuery query = new ObjectQuery(sel.getResultSet(),db2,elemCls);
            Object elem = null;
            while((elem = query.next())!=null){
                coll.add(elem);
            }
            setter.invoke(instance,coll);
            db2.close();
        }catch (DBQuaryException e1){
          throw e1;
        } catch (Exception e2){
            throw new DBQuaryException(e2);
        }
    }
    private String whereSql(T instance, TableField foreign) throws DBQuaryException {
        try{
            ClassVisitor<T> visitor = new ClassVisitor<T>(cls);
            Method keyGetter = visitor.getGetter(cls.getDeclaredField(meta.getKey().getNameInClass()));
            Object key = keyGetter.invoke(instance);
            String value = key.toString();
            if(key instanceof CharSequence){
                value = String.format("'%s'",value);
            }
            return foreign.getName()+" = "+value;
        }catch (Exception e){
            throw new DBQuaryException(e);
        }
    }
}
