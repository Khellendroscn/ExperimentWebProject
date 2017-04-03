package test;

import net.khe.db2.*;
import net.khe.db2.annotations.*;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by hyc on 2017/4/3.
 */
@DBTable
public class People {
    @SqlInt
    @Constraints(primaryKey = true,alloNull = false,autoIncrement = true)
    private int id;
    @SqlString
    private String name;
    public People(String name){
        setName(name);
    }
    public People(){}
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    @Override
    public String toString(){
        return "People("+getId()+", "+getName()+")";
    }
    public static void main(String[] args) {
        People p1 = new People("ddd");
        p1.setId(1);
        People p2 = new People("eee");
        People p3 = new People("fff");
        try {
            DBConfig config = new DBConfig("test/DbConfig.txt");
            DataBase<People> db = new DataBase<>(config,People.class);
            db.connect();
            db.create();
            DBSession<People> session = db.createSession();
            session.put(p1);
            session.put(p2);
            session.put(p3);
            session.execute();
            db.query().stream().forEach(System.out::println);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeyNotFoundException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (DBWriteException e) {
            e.printStackTrace();
        } catch (DBQuaryException e) {
            e.printStackTrace();
        }
    }
}
