package net.khe.db;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by hyc on 2017/3/16.
 */
public class Table<T extends Data> implements Iterable<T> {
    private ResultSet rs;
    private DataFactory<T> factory;
    private Map<String,String> fields = new LinkedHashMap<>();
    private String name;

    public Table(String name,ResultSet rs,DataFactory<T> factory) throws SQLException{
        this.rs = rs;
        this.factory = factory;
        this.name = name;
        initFields();
    }
    private void initFields() throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int n = metaData.getColumnCount();
        for(int i=1;i<=n;++i){
            fields.put(metaData.getColumnName(i),metaData.getColumnTypeName(i));
        }
    }

    public Map<String,String> getFields(){return fields;}
    public Map<String,Object> toMap() throws SQLException {
        Map<String,Object> map = new HashMap<>();
        int i=1;
        for(String fieldName:fields.keySet()){
            map.put(fieldName,rs.getObject(i++));
        }
        return map;
    }
    @Override
    public Iterator<T> iterator() {
        return new Iter();
    }

    public String getName() {
        return name;
    }

    class Iter implements Iterator<T>{

        @Override
        public boolean hasNext() {
            try {
                return rs.next();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public T next() {
            try {
                return factory.create(toMap());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
