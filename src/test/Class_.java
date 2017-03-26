package test;

import net.khe.db2.annotations.*;

import java.util.Arrays;
import java.util.List;

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
    @Container(elementType = "test.Stu", containerType = "java.util.ArrayList")
    private List<Stu> stus;
    public Class_(){}
    public Class_(String id,String teacher,List<Stu> stus){
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

    public List<Stu> getStus() {
        return stus;
    }

    public void setStus(List<Stu> stus) {
        this.stus = stus;
    }
    @Override
    public String toString(){
        return id + stus.toString();
    }
}
