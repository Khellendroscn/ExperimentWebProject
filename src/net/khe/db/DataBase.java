package net.khe.db;

import java.io.IOException;
import java.sql.*;
import java.util.*;

import static java.util.stream.Collectors.*;

/**
 * Created by hyc on 2017/3/16.
 */
public class DataBase<T extends Data> {
    private DbConfig config;
    private DataFactory<T> factory;
    private Connection conn;

    public DataBase(DbConfig config,DataFactory<T> factory) throws ClassNotFoundException {
        this.config = config;
        this.factory = factory;
        Class.forName(config.driver);
    }
    public void connect() throws SQLException {
        conn = DriverManager.getConnection(config.url,config.user,config.passwd);
    }
    public void close() throws SQLException {conn.close();}
    public Table<T> select(String tbName) throws SQLException {
        ResultSet rs = executeQuery(makeSelectSql(tbName));
        return new Table<T>(tbName,rs,factory);
    }
    public void insert(String tbName,T data) throws SQLException {
        PreparedStatement stmt =
                conn.prepareStatement(makeInsertSql(tbName,data));
        Map<String,Object> map = data.toMap();
        int i=1;
        for(String key:map.keySet()){
            stmt.setObject(i++,map.get(key));
        }
        stmt.executeUpdate();
    }
    public void create(String tbName,String primaryKey) throws SQLException {
        System.out.println(makeCreateSql(tbName,primaryKey));
        //execute(makeCreateSql(tbName,primaryKey));
    }
    public ResultSet executeQuery(String sql) throws SQLException {
        Statement stmt = conn.createStatement();
        return stmt.executeQuery(sql);
    }
    public void execute(String sql)throws SQLException{
        Statement stmt = conn.createStatement();
        stmt.execute(sql);
    }
    private String makeSelectSql(String name){
        String sql = "SELECT "+
                factory.getFields().keySet().stream()
                .collect(joining(", "))+
                " FROM "+name;
        return sql;
    }
    private String makeInsertSql(String name,T data) throws SQLException {
        Map<String,Object> map = data.toMap();
        String sql = "INSERT INTO "+name+"( "+
                map.keySet().stream()
                .collect(joining(", "))+
                " ) VALUES( "+
                map.keySet().stream()
                .map(key->"?")
                .collect(joining(", "))+
                " )";
        return sql;
    }
    private String makeCreateSql(String name,String primaryKey){
        Map<String,String> fields = factory.getFields();
        String sql = "CREATE TABLE "+name+"(\n"+
                fields.keySet().stream()
                .map(key->"\t"+key.toString()+" "+fields.get(key).toString())
                .collect(joining(",\n"));
        if(primaryKey!=null){
            sql+=String.format(",\n\tPRIMARY KEY( %s )",primaryKey);
        }
        sql+=("\n)");
        return sql;
    }
    public static void main(String[] args) {
        class Stu implements Data{
            public final String id;
            public final String name;
            public final int age;

            public Stu(String id, String name, int age) {
                this.id = id;
                this.name = name;
                this.age = age;
            }
            public String toString(){
                return String.format("Stu(%s,%s,%d)",id,name,age);
            }
            @Override
            public Map<String, Object> toMap() {
                Map<String,Object> map = new HashMap<>();
                map.put("id",id);
                map.put("name",name);
                map.put("age",age);
                return map;
            }
        }
        class StuFactory implements DataFactory<Stu>{
            private Map<String,String> fields = new HashMap<>();
            {
                fields.put("id","VARCHAR(5)");
                fields.put("name","VARCHAR(25)");
                fields.put("age","INT");
            }
            @Override
            public Stu create(Map<String, Object> props) {
                String id = (String)props.get("id");
                String name = (String)props.get("name");
                int age = (int)props.get("age");
                return new Stu(id,name,age);
            }

            @Override
            public Map<String, String> getFields() {
                return fields;
            }
        }

        try {
            DbConfig config = new DbConfig("test/DBConfig.txt");
            DataBase<Stu> db = new DataBase<Stu>(config,new StuFactory());
            db.connect();
            String tbname = "student";
            db.create("student3","id");
            //db.insert(tbname,new Stu("00004","ddd",19));
            Table<Stu> tb = db.select(tbname);
            for(Stu s:tb){
                System.out.println(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
