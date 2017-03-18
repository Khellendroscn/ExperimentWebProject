package test;

import net.khe.db2.DBConfig;
import net.khe.db2.DataBase;
import net.khe.db2.annotations.KeyNotFoundException;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

/**
 * Created by hyc on 2017/3/18.
 */
public class _TestUpdate {
    public static void main(String[] args) {
        try {
            DBConfig config = new DBConfig("test/DbConfig.txt");
            DataBase<Stu> db = new DataBase<>(config, Stu.class);
            db.connect();
            //db.delete("00005");
            Stu stu = new Stu("00001","aaa",20,"A2");
            db.update(stu);
            Stu aaa = db.getInstance("00001");
            System.out.println(aaa);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (KeyNotFoundException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }
}
