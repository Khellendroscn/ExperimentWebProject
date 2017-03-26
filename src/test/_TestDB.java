package test;

import net.khe.db2.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by hyc on 2017/3/21.
 */
public class _TestDB {
    public static void main(String[] args) {
        try{
            DBConfig config = new DBConfig("test/DbConfig.txt");
            DBPool pool = new DBPool();
            DataBase<Class_> db = pool.newDataBase(config,Class_.class);
            List<Stu> stus = new ArrayList<>(Arrays.asList(
                    new Stu("00008","iii",21,"B1"),
                    new Stu("00009","jjj",22,"B1"),
                    new Stu("00010","kkk",20,"B1")
            ));
            Class_ B1 = new Class_("B1","ijk",stus);
            DBSession<Class_> session = db.createSession();
            session.put(B1);
            session.execute();
            DBQuery<Class_> quary = db.query();
            quary.stream().forEach(System.out::println);
            System.out.println("end");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
