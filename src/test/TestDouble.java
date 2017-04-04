package test;

import net.khe.db2.DBConfig;
import net.khe.db2.DataBase;
import net.khe.db2.annotations.*;

import java.math.BigDecimal;

/**
 * Created by hyc on 2017/4/4.
 */
@DBTable
public class TestDouble {
    @Constraints(primaryKey = true, alloNull = false, autoIncrement = true)
    @SqlInt
    private int id;
    @SqlNumeric(size = 10, d = 2)
    private BigDecimal d;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public static void main(String[] args) {
        try {
            DBConfig config = new DBConfig("test/DbConfig.txt");
            DataBase<TestDouble> db = new DataBase<>(config,TestDouble.class);
            db.connect();
            db.create();
            TestDouble test = new TestDouble();
            test.setD(new BigDecimal(2.2));
            db.put(test);
            db.query().stream().map(t->t.getD()).forEach(System.out::println);
            db.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BigDecimal getD() {
        return d;
    }

    public void setD(BigDecimal d) {
        this.d = d;
    }
}
