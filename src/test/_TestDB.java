package test;

import net.khe.db2.*;

/**
 * Created by hyc on 2017/3/21.
 */
public class _TestDB {
    public static void main(String[] args) {
        Stu[] stus1 = {
                new Stu("00001","aaa",20,"A1"),
                new Stu("00002","bbb",21,"A1"),
                new Stu("00004","ccc",22,"A1")
        };
        Stu[] stus2 = {
                new Stu("00003","ddd",21,"A2"),
                new Stu("00005","eee",20,"A2")
        };
        Class_ a1 = new Class_("A1","Tom",stus1);
        Class_ a2 = new Class_("A2","Amy",stus2);
        try{
            DBConfig config = new DBConfig("test/DbConfig.txt");
            DBPool pool = new DBPool();
            DataBase<Class_> db = pool.newDataBase(config,Class_.class);
            DBSession<Class_> session = db.createSession();
            session.put(a1);
            session.put(a2);
            session.execute();
            Table<Class_> tb = db.getTable();
            tb.getList().stream().forEach(System.out::println);
            db.close();
            pool.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
