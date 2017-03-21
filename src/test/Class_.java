package test;

import net.khe.db2.annotations.Constraints;
import net.khe.db2.annotations.DBTable;
import net.khe.db2.annotations.SqlChars;
import net.khe.db2.annotations.SqlString;

import java.util.Arrays;

/**
 * Created by hyc on 2017/3/18.
 */
@DBTable({"Class"})
public class Class_ {
    @SqlChars(2)
    @Constraints(primaryKey = true,alloNull = false)
    private String id;
    @SqlString
    private String teacher;
    private Stu[] stus;
    public Class_(){}
    public Class_(String id,String teacher,Stu[] stus){
        this.id = id;
        this.teacher = teacher;
        this.stus = stus;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTeacher() {
        return teacher;
    }

    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }

    public Stu[] getStus() {
        return stus;
    }

    public void setStus(Stu[] stus) {
        this.stus = stus;
    }
    @Override
    public String toString(){
        return id+ Arrays.toString(stus);
    }
}
