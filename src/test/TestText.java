package test;

import net.khe.db2.DBConfig;
import net.khe.db2.DataBase;
import net.khe.db2.annotations.Constraints;
import net.khe.db2.annotations.DBTable;
import net.khe.db2.annotations.SqlInt;
import net.khe.db2.annotations.SqlText;

/**
 * Created by hyc on 2017/4/4.
 */
@DBTable
public class TestText {
    @SqlText
    private String text;
    @SqlInt
    @Constraints(primaryKey = true,alloNull = false,autoIncrement = true)
    private int id;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public static void main(String[] args) {
        try{
            DBConfig config = new DBConfig("test/DbConfig.txt");
            DataBase<TestText> db = new DataBase<>(config,TestText.class);
            db.connect();
            db.create();
            TestText test = new TestText();
            test.setText("wwwww");
            db.put(test);
            db.query().stream().map(t->t.getText()).forEach(System.out::println);
            db.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
