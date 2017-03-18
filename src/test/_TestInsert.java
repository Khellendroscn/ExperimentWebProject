package test;

import net.khe.db2.DBConfig;
import net.khe.db2.DataBase;
import net.khe.db2.Table;
import net.khe.db2.annotations.KeyNotFoundException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

/**
 * Created by hyc on 2017/3/18.
 */
public class _TestInsert {
    public static void main(String[] args) {
        Stu[] stuA1 = {
                new Stu("00001","aaa",20,"A1"),
                new Stu("00002","bbb",21,"A1"),
                new Stu("00004","ccc",20,"A1")
        };
        Stu[] stuA2 = {
                new Stu("00003","ddd",21,"A2"),
                new Stu("00005","eee",22,"A2")
        };
        Class_ a1 = new Class_("A1","Lee",stuA1);
        Class_ a2 = new Class_("A2","wang",stuA2);
        try {
            DBConfig config = new DBConfig("test/DbConfig.txt");
            DataBase<Stu> dbStu = new DataBase<>(config,Stu.class);
            dbStu.connect();
            /*dbStu.create();
            for(Stu s:stuA1){
                dbStu.insert(s);
            }
            for(Stu s:stuA2){
                dbStu.insert(s);
            }
            DataBase<Class_> dbClass = new DataBase<>(config,Class_.class);
            dbClass.connect();;
            dbClass.create();
            dbClass.insert(a1);
            dbClass.insert(a2);*/
            Table<Stu> tb = dbStu.getTable();
            for(Stu s:tb){
                System.out.println(s);
            }
            dbStu.close();
            //dbClass.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeyNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }
}
